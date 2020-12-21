package cz.vaclavtolar.volebnizavod.activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.util.ClassUtil.getPackageName
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_ID
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_NAME
import cz.vaclavtolar.volebnizavod.R
import cz.vaclavtolar.volebnizavod.dto.ElectionData
import cz.vaclavtolar.volebnizavod.dto.Strana
import cz.vaclavtolar.volebnizavod.service.ServerService
import cz.vaclavtolar.volebnizavod.util.ElectionPartyMappings
import cz.vaclavtolar.volebnizavod.util.Party
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ElectionActivity : AppCompatActivity() {

    private lateinit var partiesAdapter: PartiesAdapter

    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_election)

        id = intent.getStringExtra(ELECTION_ID).toInt()
        supportActionBar!!.setTitle(intent.getStringExtra(ELECTION_NAME))

        partiesAdapter = PartiesAdapter()
        val itemsRecyler = findViewById<RecyclerView>(R.id.parties)
        itemsRecyler.adapter = partiesAdapter
        val layoutManager = LinearLayoutManager(this)
        itemsRecyler.layoutManager = layoutManager

    }

    override fun onStart() {
        super.onStart()
        val call: Call<ElectionData>? = id?.let { ServerService.getInstance().getElection(it) }
        call?.enqueue(object : Callback<ElectionData> {
            override fun onResponse(
                call: Call<ElectionData>,
                response: Response<ElectionData>
            ) {
                Log.d("srv_call", "Successfully got election detail from server")
                partiesAdapter.parties = response.body()?.cr?.strana!!
                partiesAdapter.parties =
                    partiesAdapter.parties.sortedByDescending { it.nazstr }.sortedByDescending { it.hodnotystrana?.prochlasu }
                partiesAdapter.maxVotesPercent = partiesAdapter.parties.maxByOrNull { it.hodnotystrana?.prochlasu!! }?.hodnotystrana?.prochlasu
                partiesAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<ElectionData>, t: Throwable) {
                Log.e("srv_call", "Failed to get election detail from server", t)
            }
        })
    }


    class PartiesAdapter : RecyclerView.Adapter<PartiesAdapter.ViewHolder>() {

        var maxVotesPercent: Double? = null
        var parties: List<Strana> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView:View = LayoutInflater.from(parent.context)
                .inflate(R.layout.party_result, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val itemView: View = holder.itemView
            val partyNameTextView: TextView = itemView.findViewById(R.id.party_name)
            partyNameTextView.setText(parties.get(position).nazstr)

            val partyVotesPercentTextView: TextView = itemView.findViewById(R.id.party_votes_percents)
            partyVotesPercentTextView.setText(parties.get(position).hodnotystrana?.prochlasu.toString())

            val partyStripe: StripeView = itemView.findViewById(R.id.party_stripe)
            partyStripe.maxVotesPercent = maxVotesPercent!!
            partyStripe.strana = parties.get(position)
        }

        override fun getItemCount(): Int {
            return parties.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }

    class StripeView(
        context: Context,
        attributeSet: AttributeSet? = null
    )
        : View(context, attributeSet) {

        var maxVotesPercent: Double? = null
        var strana: Strana? = null
        private var viewHeight: Int = 0
        private var viewWidth: Int = 0

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            val paint = Paint()
            paint.setStyle(Paint.Style.FILL);
            val party: Party? = ElectionPartyMappings.SNEMOVNA_2017.get(strana?.kstrana)
            paint.setColor(party?.color!!)
            val stripeWidth = (strana?.hodnotystrana?.prochlasu?.div(maxVotesPercent!!))?.times(viewWidth!!)
            canvas?.drawRect(0F,0F, stripeWidth?.toFloat()!!, viewHeight.toFloat(), paint)
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            this.viewWidth = w
            this.viewHeight = h
        }
    }

}