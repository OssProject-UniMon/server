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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scholar)
        val userId = AppData.S_userId

        //secure무시, 리트로핏 통신까지
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://43.202.82.18:443")
        val ActService = retrofit.create(ManageService::class.java)

        val dynamicUrl2 = "/scholarship/recommend?userId=$userId"
        val call = ActService.price_list(dynamicUrl2)
        call.enqueue(object : Callback<price_listResponse> {
            override fun onResponse(call: Call<price_listResponse>, response: Response<price_listResponse>) {
                val logs = response.body()?.scholarshipList
                Log.d("priceResult: ", "Response: $logs")

            }

            override fun onFailure(call: Call<price_listResponse>, t: Throwable) {

                Log.e("price_showlist", "Failed to send request to server. Error: ${t.message}")
            }
        })

        val recyclerView: RecyclerView = findViewById(R.id.resultview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = S_adapter(getData())
        val testbutton = findViewById<Button>(R.id.shobtn1)

        testbutton.setOnClickListener {
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

        // Divider 제거
        recyclerView.addItemDecoration(NoDividerItemDecoration(this))
    }

    private fun getData(): List<S_data> {
        return listOf(
            S_data("Title 1", "2024-06-01", "Content 1",arrayOf("지원대상1", "지원내용1", "신청기간1", "신청방법1")),
            S_data("Title 2", "2024-06-02", "Content 2",arrayOf("지원대상1", "지원내용1", "신청기간1", "신청방법1")),
            S_data("Title 3", "2024-06-03", "Content 3",arrayOf("지원대상1", "지원내용1", "신청기간1", "신청방법1"))
        )
    }
}