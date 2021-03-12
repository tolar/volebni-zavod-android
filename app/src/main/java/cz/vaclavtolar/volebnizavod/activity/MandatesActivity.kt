package cz.vaclavtolar.volebnizavod.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.View.*
import android.widget.*
import android.widget.TableLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devs.vectorchildfinder.VectorChildFinder
import com.google.android.material.navigation.NavigationView
import cz.vaclavtolar.volebnizavod.R
import cz.vaclavtolar.volebnizavod.dto.ElectionData
import cz.vaclavtolar.volebnizavod.dto.Strana
import cz.vaclavtolar.volebnizavod.service.PreferencesUtil
import cz.vaclavtolar.volebnizavod.service.ServerService
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_ID
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_NAME
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_YEAR
import cz.vaclavtolar.volebnizavod.util.PartyMappings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt


class MandatesActivity : ElectionActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var partiesAdapter: PartiesAdapter
    private var coalitionParties = mutableSetOf<Strana>()

    companion object {
        private fun getPersonImageView(
            context: Context,
            displayMetrics: DisplayMetrics
        ): Pair<ImageView, VectorChildFinder> {

            val minus5dipAsPixels:Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                -5f,
                displayMetrics
            ).roundToInt()

            val imageView = ImageView(context)
            imageView.setImageResource(R.drawable.ic_baseline_person_36)
            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            imageView.minimumHeight = 0
            imageView.minimumWidth = 0

            lp.setMargins(minus5dipAsPixels, minus5dipAsPixels, 0, 0)
            imageView.layoutParams = lp

            val vector = VectorChildFinder(
                context,
                R.drawable.ic_baseline_person_36,
                imageView
            )

            return Pair(imageView, vector)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_election_mandates)

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


    }

    override fun onStart() {
        super.onStart()
        supportActionBar!!.setTitle(name + " - " + "mandáty")

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
            }

            override fun onFailure(call: Call<ElectionData>, t: Throwable) {
                Log.e("srv_call", "Failed to get election detail from server", t)
            }
        })

        val electionsData = PreferencesUtil.getElectionsDataFromPreferences(applicationContext)?.electionsData?.get(
            id
        )
        if (electionsData != null) {
            updateGui(electionsData)
        }



    }

    private fun updateGui(electionData: ElectionData?) {

        var parties = electionData?.cr?.strana!!
        parties = parties.filter { it.hodnotystrana?.mandaty != null &&  it.hodnotystrana?.mandaty!! >= 1}
        parties =
            parties.sortedByDescending { it.nazstr }
                .sortedByDescending { it.hodnotystrana?.mandaty }

        updateHeader(electionData)
        updatePartiesAdapter(parties)
        updateCoalitionParties(parties)
        updateHouse()
    }

    private fun updatePartiesAdapter(parties: List<Strana>) {
        val maxMandates =
            parties.maxByOrNull { it.hodnotystrana?.mandaty!! }?.hodnotystrana?.mandaty

        partiesAdapter.parties = parties
        partiesAdapter.maxMandates = maxMandates

        partiesAdapter.notifyDataSetChanged()
    }

    private fun updateHeader(electionData: ElectionData?) {

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


    }

    private fun updateCoalitionParties(parties: List<Strana>) {
        val coalitionPartiesView = findViewById<ViewGroup>(R.id.coalition_parties)
        coalitionPartiesView.removeAllViews()
        parties.forEach {
            val partyToggle = ToggleButton(applicationContext)
            val party = partiesMap?.get(it.kstrana)
            val strana = it
            partyToggle.text = party?.abbr
            partyToggle.textOff = party?.abbr
            partyToggle.textOn = party?.abbr
            partyToggle.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    coalitionParties.add(strana)
                } else {
                    coalitionParties.remove(strana)
                }
                updateHouse()
            }
            coalitionPartiesView.addView(partyToggle)
        }
    }

    private fun updateHouse() {
        val house = findViewById<ViewGroup>(R.id.house)
        house.removeAllViews()

        val sortedCoalitionParties = coalitionParties.sortedByDescending { it.hodnotystrana?.mandaty }
        val mandateParty = mutableListOf<Strana>()
        var prevPartiesMandates: Int = 0
        sortedCoalitionParties.forEach {
            for (i in 0 .. (it.hodnotystrana?.mandaty!! - 1)) {
                mandateParty.add(prevPartiesMandates + i, it)
            }
            prevPartiesMandates = mandateParty.size
        }

        for (i in 1 .. 10) {
            val tableRow = TableRow(applicationContext)
            tableRow.orientation = TableRow.VERTICAL
            val tableRowParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            tableRow.setLayoutParams(tableRowParams)

            for (j in 1 .. 20) {
                val (imageView, vector) = getPersonImageView(
                    applicationContext,
                    resources.displayMetrics,
                )
                imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                imageView.adjustViewBounds = true
                val lp: LinearLayout.LayoutParams =
                    TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1F
                    )
                imageView.layoutParams = lp

                var partyColor:Int? = Color.LTGRAY
                val index = ((i-1) * 20 + j) - 1
                if (index <= mandateParty.lastIndex) {
                    partyColor = partiesMap?.get(mandateParty[index].kstrana)?.color
                }

                vector.findPathByName("person").fillColor = partyColor!!
                tableRow.addView(imageView)
            }
            house.addView(tableRow)
        }

    }

    fun toggleMoreParties(view: View) {
        val restPartiesView: View = findViewById(R.id.rest_parties_wrapper)
        val morePartiesBtn: Button = findViewById(R.id.more_parties_btn)
        val currentVisibility = restPartiesView.visibility
        if (currentVisibility == VISIBLE) {
            restPartiesView.visibility = GONE
            morePartiesBtn.text = "Zobrazit další strany"
        } else if (currentVisibility == INVISIBLE || currentVisibility == GONE) {
            restPartiesView.visibility = VISIBLE
            morePartiesBtn.text = "Skrýt další strany"
        }
    }


    class PartiesAdapter : RecyclerView.Adapter<PartiesAdapter.ViewHolder>() {

        var maxMandates: Int? = null
        var parties: List<Strana> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView: View = inflater
                .inflate(R.layout.party_mandates, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val itemView: View = holder.itemView
            val partyNameTextView: TextView = itemView.findViewById(R.id.party_name)
            val strana = parties.get(position)
            val party = partiesMap?.get(strana.kstrana)
            partyNameTextView.setText(party?.abbr)
            val partyMandatesPercent: TextView = itemView.findViewById(R.id.party_mandates_percent)
            partyMandatesPercent.setText(formatterForMandatesPercentValue.format(strana.hodnotystrana?.procmandatu) + " % mandátů")

            val partyMandatesView: TextView =
                itemView.findViewById(R.id.party_value)
            strana.hodnotystrana?.mandaty?.let { partyMandatesView.setText(it.toString()) }

            val personsWrapper = itemView.findViewById<ViewGroup>(R.id.persons_wrapper)
            val mandates = strana.hodnotystrana?.mandaty

            personsWrapper.removeAllViews()
            for (i in 1..mandates!!) {
                val (imageView, vector) = getPersonImageView(
                    itemView.context,
                    itemView.resources.displayMetrics
                )

                vector.findPathByName("person").fillColor = party?.color!!
                personsWrapper.addView(imageView)
            }
        }


        override fun getItemCount(): Int {
            return parties.size
        }

        private fun getFormattedPercentValue(percentVotes: Double?): String {
            return formatterForVotesPercentValue.format(percentVotes) + "%"
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }






}

