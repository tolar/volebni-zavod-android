package cz.vaclavtolar.volebnizavod.activity

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.*
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
import cz.vaclavtolar.volebnizavod.service.ServerService
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_ID
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_NAME
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_YEAR
import cz.vaclavtolar.volebnizavod.util.PartyMappings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MandatesActivity : ElectionActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var partiesAdapter: PartiesAdapter

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
        supportActionBar!!.setTitle(intent.getStringExtra(ELECTION_NAME))
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
                updatePartiesAdapter(response)
                updateMainDataGui(response)
            }

            override fun onFailure(call: Call<ElectionData>, t: Throwable) {
                Log.e("srv_call", "Failed to get election detail from server", t)
            }
        })



    }

    private fun updatePartiesAdapter(response: Response<ElectionData>) {
        var parties = response.body()?.cr?.strana!!
        parties = parties.filter { it.hodnotystrana?.mandaty != null &&  it.hodnotystrana?.mandaty!! >= 1}
        parties =
            parties.sortedByDescending { it.nazstr }
                .sortedByDescending { it.hodnotystrana?.mandaty }
        val maxMandates =
            parties.maxByOrNull { it.hodnotystrana?.mandaty!! }?.hodnotystrana?.mandaty

        partiesAdapter.parties = parties
        partiesAdapter.maxMandates = maxMandates

        partiesAdapter.notifyDataSetChanged()
    }

    private fun updateMainDataGui(response: Response<ElectionData>) {

        findViewById<TextView>(R.id.counted).text = formatter.format(response.body()?.cr?.ucast?.okrskyzpracproc) + "%"
        findViewById<TextView>(R.id.attendance).text = formatter.format(response.body()?.cr?.ucast?.ucastproc) + "%"

        findViewById<View>(R.id.counted_label).visibility = VISIBLE
        findViewById<View>(R.id.counted).visibility = VISIBLE
        findViewById<View>(R.id.attendance_label).visibility = VISIBLE
        findViewById<View>(R.id.attendance).visibility = VISIBLE


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
            partyNameTextView.setText(strana.nazstr)

            val partyMandatesView: TextView =
                itemView.findViewById(R.id.party_value)
            strana.hodnotystrana?.mandaty?.let { partyMandatesView.setText(it.toString()) }

            val personsWrapper = itemView.findViewById<GridLayout>(R.id.persons_wrapper)

            val mandates = strana.hodnotystrana?.mandaty
            for (i in 1..mandates!!) {
                val imageView = ImageView(itemView.context)
                imageView.setImageResource(R.drawable.ic_baseline_person_36)
                imageView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val vector = VectorChildFinder(itemView.context, R.drawable.ic_baseline_person_36, imageView)
                val party = partiesMap?.get(strana.kstrana)
                vector.findPathByName("person").fillColor = party?.color!!
                personsWrapper.addView(imageView)
            }

        }

        override fun getItemCount(): Int {
            return parties.size
        }

        private fun getFormattedPercentValue(percentVotes: Double?): SpannableStringBuilder {

            val formattedValue: String = formatter.format(percentVotes) + " %"
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
            morePartiesBtn.text = "Zobrazit další strany"
        } else if (currentVisibility == INVISIBLE || currentVisibility == GONE) {
            restPartiesView.visibility = VISIBLE
            morePartiesBtn.text = "Skrýt další strany"
        }
    }


}