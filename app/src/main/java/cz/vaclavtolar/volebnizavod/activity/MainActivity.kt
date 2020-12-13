package cz.vaclavtolar.volebnizavod.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.vaclavtolar.volebnizavod.R
import cz.vaclavtolar.volebnizavod.dto.Election
import cz.vaclavtolar.volebnizavod.service.ServerService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val call: Call<List<Election>> = ServerService.getInstance().allElections
        call.enqueue(object: Callback < List < Election > > {
            override fun onResponse(call: Call<List<Election>>, response: Response<List<Election>>) {
                //TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<List<Election>>, t: Throwable) {
                //TODO("Not yet implemented")
            }
        })
    }
}