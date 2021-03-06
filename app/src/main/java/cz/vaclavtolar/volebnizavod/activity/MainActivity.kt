package cz.vaclavtolar.volebnizavod.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_ID
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_NAME
import cz.vaclavtolar.volebnizavod.R
import cz.vaclavtolar.volebnizavod.dto.CachedElections
import cz.vaclavtolar.volebnizavod.dto.Election
import cz.vaclavtolar.volebnizavod.service.PreferencesUtil
import cz.vaclavtolar.volebnizavod.service.ServerService
import cz.vaclavtolar.volebnizavod.util.Constants.ELECTION_YEAR
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ElectionActivity() {


    private lateinit var electionsAdapter: ElectionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        electionsAdapter = ElectionsAdapter()
        val itemsRecyler = findViewById<RecyclerView>(R.id.elections)
        itemsRecyler.adapter = electionsAdapter
        val layoutManager = LinearLayoutManager(this)
        itemsRecyler.layoutManager = layoutManager

    }

    override fun onStart() {
        super.onStart()
        val call: Call<List<Election>> = ServerService.getInstance().allElections
        call.enqueue(object : Callback<List<Election>> {
            override fun onResponse(
                call: Call<List<Election>>,
                response: Response<List<Election>>
            ) {
                Log.d("srv_call", "Successfully got elections from server")
                elections = response.body()!!
                elections = elections.sortedByDescending { it.date }
                electionsAdapter.elections = elections
                electionsAdapter.notifyDataSetChanged()
                PreferencesUtil.storeElectionsToPreferences(applicationContext, CachedElections(elections))
            }

            override fun onFailure(call: Call<List<Election>>, t: Throwable) {
                Log.e("srv_call", "Failed to get elections from server", t)
            }
        })
        val cachedElectionData = PreferencesUtil.getElectionsFromPreferences(applicationContext)
        if (cachedElectionData?.elections != null) {
            elections = cachedElectionData.elections!!
            electionsAdapter.elections = cachedElectionData.elections!!
            electionsAdapter.notifyDataSetChanged()
        }
    }

    class ElectionsAdapter : RecyclerView.Adapter<ElectionsAdapter.ViewHolder>() {

        var elections: List<Election> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val button = LayoutInflater.from(parent.context)
                .inflate(R.layout.election_button, parent, false) as Button
            return ViewHolder(button)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val itemView: View = holder.itemView
            val button: Button = itemView.findViewById(R.id.button)
            button.setText(elections.get(position).name)
            if (position > 0) {
                button.setBackgroundColor(itemView.resources.getColor(R.color.primaryLightColor))
            }
            button.setOnClickListener { startElectionActivity(itemView.context, position) }
        }

        private fun startElectionActivity(context: Context, position: Int) {
            val intent = Intent(context, VotesActivity::class.java).apply {
                val election = elections.get(position)
                putExtra(ELECTION_ID, election.id)
                putExtra(ELECTION_NAME, election.name)
                putExtra(ELECTION_YEAR, election.date?.year)
            }
            context.startActivity(intent)
        }

        override fun getItemCount(): Int {
            return elections.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    }



}