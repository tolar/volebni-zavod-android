package cz.vaclavtolar.volebnizavod.activity

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.vaclavtolar.volebnizavod.Constants.ELECTION_ID
import cz.vaclavtolar.volebnizavod.Constants.ELECTION_NAME
import cz.vaclavtolar.volebnizavod.R
import cz.vaclavtolar.volebnizavod.dto.ElectionData
import cz.vaclavtolar.volebnizavod.dto.Strana
import cz.vaclavtolar.volebnizavod.service.ServerService
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
                    partiesAdapter.parties.sortedByDescending { it.hodnotystrana?.prochlasu }
                partiesAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<ElectionData>, t: Throwable) {
                Log.e("srv_call", "Failed to get election detail from server", t)
            }
        })
    }


    class PartiesAdapter : RecyclerView.Adapter<PartiesAdapter.ViewHolder>() {

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
        }

        override fun getItemCount(): Int {
            return parties.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }

    class StripeView constructor(context: Context, attributeSet: AttributeSet? = null)
        : View(context, attributeSet) {

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            val paint = Paint()
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            //this.layoutParams.width = 200
            canvas?.drawRect(0F,0F,100F,20F, paint)
        }
    }

}