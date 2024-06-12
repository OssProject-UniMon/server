package com.example.why1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.why1.appdata.AppData
import com.example.why1.appdata.NoDividerItemDecoration
import com.example.why1.appdata.S_adapter
import com.example.why1.appdata.S_data
import com.example.why1.appdata.Schedule
import com.example.why1.auth.NetworkConnection
import com.example.why1.retropit.JoinResponse
import com.example.why1.retropit.ManageService
import com.example.why1.retropit.Sch_listResponse
import com.example.why1.retropit.price_listResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScholarActivity : AppCompatActivity() {
    private val scholarshipList = mutableListOf<S_data>()
    private lateinit var adapter: S_adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scholar)

        adapter = S_adapter(scholarshipList)
        val recyclerView: RecyclerView = findViewById(R.id.resultview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        val testbutton = findViewById<Button>(R.id.shobtn1)
        val renew_btn = findViewById<Button>(R.id.policybtn2)

        testbutton.setOnClickListener {
            val okHttpClient = NetworkConnection.createOkHttpClient()
            val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://43.202.82.18:443")
            val ActService = retrofit.create(ManageService::class.java)
            val dynamicUrl3 = "/scholarship/scrape"
            val call2 = ActService.test(dynamicUrl3)
            call2.enqueue(object : Callback<JoinResponse> {
                override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
                    val logs2 = response.body()
                    Log.d("testResult: ", "Response: $logs2")

                }

                override fun onFailure(call: Call<JoinResponse>, t: Throwable) {

                    Log.e("test", "Failed to send request to server. Error: ${t.message}")
                }
            })
        }

        renew_btn.setOnClickListener {
            fetchData()
        }
    }

    private fun fetchData() {
        val userId = AppData.S_userId
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://43.202.82.18:443")
        val ActService = retrofit.create(ManageService::class.java)

        val dynamicUrl2 = "/scholarship/recommend?userId=$userId"
        val call = ActService.price_list(dynamicUrl2)
        call.enqueue(object : Callback<price_listResponse> {
            override fun onResponse(call: Call<price_listResponse>, response: Response<price_listResponse>) {
                val logs = response.body()?.scholarshipList
                logs?.let {
                    for (scholarship in it) {
                        val detailsArray = arrayOf(
                            scholarship.amount,
                            scholarship.target,
                            scholarship.url,
                            "" // 빈 문자열 추가
                        )
                        val sData = S_data(
                            scholarship.name,
                            scholarship.due,
                            "", // content는 비어있음
                            detailsArray
                        )
                        scholarshipList.add(sData)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<price_listResponse>, t: Throwable) {
                Log.e("price_showlist", "Failed to send request to server. Error: ${t.message}")
            }
        })
    }
}