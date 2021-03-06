package cz.vaclavtolar.volebnizavod.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devs.vectorchildfinder.VectorChildFinder
import com.google.android.material.navigation.NavigationView
import cz.vaclavtolar.volebnizavod.R
import cz.vaclavtolar.volebnizavod.dto.*
import cz.vaclavtolar.volebnizavod.service.PreferencesUtil
import cz.vaclavtolar.volebnizavod.service.ServerService
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_ID
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_NAME
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_YEAR
import cz.vaclavtolar.volebnizavod.util.Mappings
import cz.vaclavtolar.volebnizavod.util.PartyMappings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VotesActivity : ElectionActivity() {

    private lateinit var partiesAdapter: PartiesAdapter
    private lateinit var restPartiesAdapter: PartiesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_election)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.nav_open_drawer,
            R.string.nav_close_drawer
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


        id = intent.getStringExtra(ELECTION_ID).toInt()
        name = intent.getStringExtra(ELECTION_NAME)
        supportActionBar!!.setTitle(name)
        year = intent.getIntExtra(ELECTION_YEAR, 0)

        if (year == 2013) {
            partiesMap = PartyMappings.SNEMOVNA_2013
        } else if (year == 2017) {
            partiesMap = PartyMappings.SNEMOVNA_2017
        } else if (year == 2010) {
            partiesMap = PartyMappings.SNEMOVNA_2010
        } else if (year == 2006) {
            partiesMap = PartyMappings.SNEMOVNA_2006
        }

        partiesAdapter = PartiesAdapter()
        val itemsRecylerParties = findViewById<RecyclerView>(R.id.parties)
        itemsRecylerParties.adapter = partiesAdapter
        itemsRecylerParties.layoutManager = LinearLayoutManager(this)

        restPartiesAdapter = PartiesAdapter()
        val itemsRecylerRestParties = findViewById<RecyclerView>(R.id.rest_parties)
        itemsRecylerRestParties.adapter = restPartiesAdapter
        itemsRecylerRestParties.layoutManager = LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        supportActionBar!!.setTitle(name)

        val callForElectionDetail: Call<ElectionData>? = id?.let {
            ServerService.getInstance().getElection(
                it
            )
        }
        callForElectionDetail?.enqueue(object : Callback<ElectionData> {
            override fun onResponse(
                call: Call<ElectionData>,
                response: Response<ElectionData>
            ) {
                Log.d("srv_call", "Successfully got election detail from server")
                updateGui(response.body())
                PreferencesUtil.storeElectionsDataToPreferences(
                    applicationContext,
                    CachedElectionsData(mutableMapOf(id!! to response.body()))
                )
            }

            override fun onFailure(call: Call<ElectionData>, t: Throwable) {
                Log.e("srv_call", "Failed to get election detail from server", t)
            }
        })

        val electionsData =
            PreferencesUtil.getElectionsDataFromPreferences(applicationContext)?.electionsData?.get(
                id
            )
        if (electionsData != null) {
            updateGui(electionsData)
        }

        val callForElectionDistrict: Call<List<ElectionDistrictData>>? = id?.let {
            ServerService.getInstance().getElectionDistricts(
                it
            )
        }
        callForElectionDistrict?.enqueue(object : Callback<List<ElectionDistrictData>> {
            override fun onResponse(
                call: Call<List<ElectionDistrictData>>,
                response: Response<List<ElectionDistrictData>>
            ) {
                Log.d("srv_call", "Successfully got election districts from server")
                updateDistrictMap(response.body())
                val cachedElectionsDistrictsData =
                    PreferencesUtil.getElectionsDistrictsDataFromPreferences(applicationContext)
                if (cachedElectionsDistrictsData?.electionDistrictsData != null) {
                    cachedElectionsDistrictsData?.electionDistrictsData?.put(
                        id!!,
                        response.body()!!
                    )
                    PreferencesUtil.storeElectionsDistrictsDataToPreferences(
                        applicationContext,
                        cachedElectionsDistrictsData
                    )
                } else {
                    PreferencesUtil.storeElectionsDistrictsDataToPreferences(
                        applicationContext, CachedElectionsDistrictsData(mutableMapOf(id!! to response.body())))
                }
            }

            override fun onFailure(call: Call<List<ElectionDistrictData>>, t: Throwable) {
                Log.e("srv_call", "Failed to get election districts from server", t)
            }
        })

        val electionDistrictsData =
            PreferencesUtil.getElectionsDistrictsDataFromPreferences(applicationContext)?.electionDistrictsData?.get(
                id
            )
        if (electionDistrictsData != null) {
            updateDistrictMap(electionDistrictsData)
        }


    }



    private fun updatePartiesAdapter(electionData: ElectionData?) {
        var parties = electionData?.cr?.strana!!
        parties =
            parties.sortedByDescending { it.nazstr }
                .sortedByDescending { it.hodnotystrana?.prochlasu }
        val maxProchlasu =
            parties.maxByOrNull { it.hodnotystrana?.prochlasu!! }?.hodnotystrana?.prochlasu

        partiesAdapter.parties = parties.filter { it.hodnotystrana?.prochlasu!! >= 1 }
        partiesAdapter.maxVotesPercent = maxProchlasu
        partiesAdapter.parties.forEachIndexed { index, strana ->
            run {
                if (strana.hodnotystrana?.prochlasu!! < 5 && partiesAdapter.lastPartyToSnemovna == null) {
                    partiesAdapter.lastPartyToSnemovna = partiesAdapter.parties[index - 1]
                }
            }
        }

        restPartiesAdapter.parties = parties.filter { it.hodnotystrana?.prochlasu!! < 1 }
        restPartiesAdapter.maxVotesPercent = maxProchlasu

        partiesAdapter.notifyDataSetChanged()
        restPartiesAdapter.notifyDataSetChanged()
    }

    private fun updateGui(electionData: ElectionData?) {
        updatePartiesAdapter(electionData)
        updateCountiesMap(electionData)
        updateMainDataGui(electionData)
    }

    private fun updateCountiesMap(electionData: ElectionData?) {
        val imageView = findViewById<ImageView>(R.id.counties_map)
        val vector = VectorChildFinder(applicationContext, R.drawable.ic_cr_kraje, imageView)
        electionData?.kraj?.forEach {
            val maxStrana = it.strana?.maxByOrNull { it.hodnotystrana?.prochlasu!! }
            val pathName = Mappings.CISKRAJ_TO_PATH[it.ciskraj]
            val party = partiesMap?.get(maxStrana?.kstrana)
            val path = vector.findPathByName(pathName)
            path.fillColor = party?.color!!
        }
        imageView.visibility = VISIBLE
        findViewById<View>(R.id.headline_counties).visibility = VISIBLE
    }

    private fun updateDistrictMap(
        electionDistrictsData: List<ElectionDistrictData>?
    ) {
        val imageView = findViewById<ImageView>(R.id.districts_map)
        val vector = VectorChildFinder(applicationContext, R.drawable.ic_cr_okresy, imageView)
        electionDistrictsData?.forEach {
            val maxStrana = it.okres?.hlasystrana?.maxByOrNull { it.prochlasu!! }
            var pathName = it.okres?.nutsokres
            var path = vector.findPathByName(pathName)
            if (path == null) {
                pathName = Mappings.DISTRICT_2006_NUTS_TO_NUTS[it.okres?.nutsokres]
                path = vector.findPathByName(pathName)
            }
            val party = partiesMap?.get(maxStrana?.kstrana)
            path?.fillColor = party?.color!!
        }
        imageView.visibility = VISIBLE
        findViewById<View>(R.id.headline_districts).visibility = VISIBLE
        findViewById<View>(R.id.districts_loading_info).visibility = GONE
    }

    @SuppressLint("CutPasteId")
    private fun updateMainDataGui(electionData: ElectionData?) {

        findViewById<TextView>(R.id.counted).text = formatterForVotesPercentValue.format(
            electionData?.cr?.ucast?.okrskyzpracproc
        ) + "%"
        findViewById<TextView>(R.id.attendance).text = formatterForVotesPercentValue.format(
            electionData?.cr?.ucast?.ucastproc
        ) + "%"

        findViewById<View>(R.id.counted_label).visibility = VISIBLE
        findViewById<View>(R.id.counted).visibility = VISIBLE
        findViewById<View>(R.id.attendance_label).visibility = VISIBLE
        findViewById<View>(R.id.attendance).visibility = VISIBLE

        findViewById<View>(R.id.more_parties_btn).visibility = VISIBLE

    }


    class PartiesAdapter : RecyclerView.Adapter<PartiesAdapter.ViewHolder>() {


        var lastPartyToSnemovna: Strana? = null
        var maxVotesPercent: Double? = null
        var parties: List<Strana> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView: View = inflater
                .inflate(R.layout.party_votes, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val itemView: View = holder.itemView
            val partyNameTextView: TextView = itemView.findViewById(R.id.party_name)
            val strana = parties.get(position)
            var partyName = strana.nazstr
            if (partiesMap?.get(strana.kstrana)?.abbr != null) {
                partyName = partiesMap?.get(strana.kstrana)?.abbr
            }
            partyNameTextView.text = partyName

            val partyVotesTextView: TextView = itemView.findViewById(R.id.party_votes)
            partyVotesTextView.text =
                " - " + formatterForVotesAbsoluteValue.format(strana.hodnotystrana?.hlasy) + " hlasů"

            val partyVotesPercentTextView: TextView =
                itemView.findViewById(R.id.party_value)
            partyVotesPercentTextView.setText(getFormattedPercentValue(strana.hodnotystrana?.prochlasu))

            val partyStripe: StripeView = itemView.findViewById(R.id.party_stripe)
            partyStripe.value = strana.hodnotystrana?.prochlasu
            partyStripe.max = maxVotesPercent!!
            partyStripe.strana = strana
            partyStripe.partiesMap = partiesMap

            if (strana.kstrana == lastPartyToSnemovna?.kstrana) {
                itemView.findViewById<View>(R.id.border).visibility = VISIBLE
            } else {
                itemView.findViewById<View>(R.id.border).visibility = GONE
            }


        }

        override fun getItemCount(): Int {
            return parties.size
        }

        private fun getFormattedPercentValue(percentVotes: Double?): SpannableStringBuilder {

            val formattedValue: String = formatterForVotesPercentValue.format(percentVotes) + " %"
            val spannableStringBuilder = SpannableStringBuilder(formattedValue)
            spannableStringBuilder.setSpan(
                RelativeSizeSpan(0.75f),
                spannableStringBuilder.length - 4,
                spannableStringBuilder.length - 1,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            return spannableStringBuilder
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }

    fun toggleMoreParties(view: View) {
        val restPartiesView: View = findViewById(R.id.rest_parties_wrapper)
        val morePartiesBtn: Button = findViewById(R.id.more_parties_btn)
        val currentVisibility = restPartiesView.visibility
        if (currentVisibility == VISIBLE) {
            restPartiesView.visibility = GONE
            morePartiesBtn.text = getString(R.string.showFurtherParties)
        } else if (currentVisibility == INVISIBLE || currentVisibility == GONE) {
            restPartiesView.visibility = VISIBLE
            morePartiesBtn.text = getString(R.string.hideFurtherParties)
        }
    }

}
