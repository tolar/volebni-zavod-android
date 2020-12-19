package cz.vaclavtolar.volebnizavod.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.vaclavtolar.volebnizavod.R
import cz.vaclavtolar.volebnizavod.dto.Election
import cz.vaclavtolar.volebnizavod.service.ServerService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

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
                Log.d("srv_call", "Successfully called server")
                electionsAdapter.elections = response.body()!!
                electionsAdapter.elections = electionsAdapter.elections.sortedByDescending { it.date }
                electionsAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<Election>>, t: Throwable) {
                Log.e("srv_call", "Failed to call server", t)
            }
        })
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
        }

        override fun getItemCount(): Int {
            return elections.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val button: Button = itemView.findViewById(R.id.button)
        }

    }
}