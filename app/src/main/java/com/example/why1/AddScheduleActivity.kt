package com.example.why1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.why1.appdata.AppData
import com.example.why1.auth.NetworkConnection
import com.example.why1.retropit.JoinResponse
import com.example.why1.retropit.ManageService
import com.example.why1.retropit.Sch_Request
import com.example.why1.retropit.act_RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)
        val userid = AppData.S_userId //유저 아이디 불러오기

        //secure무시, 리트로핏 통신까지
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://43.202.82.18:443")
        val ActService = retrofit.create(ManageService::class.java)

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val startHourEditText = findViewById<EditText>(R.id.startHourEditText)
        val endHourEditText = findViewById<EditText>(R.id.endHourEditText)
        val dayOfWeekEditText = findViewById<EditText>(R.id.dayOfWeekEditText)
        val addButton = findViewById<Button>(R.id.addButton)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val startHour = startHourEditText.text.toString().toInt()
            val endHour = endHourEditText.text.toString().toInt()
            val dayOfWeek = dayOfWeekEditText.text.toString().toInt()
            val i_startHour = startHourEditText.text.toString()
            val i_endHour = endHourEditText.text.toString()
            val i_dayOfWeek = dayOfWeekEditText.text.toString()

            //버튼 누르면 시간표 담기게
            val dynamicUrl = "/home/plus?userId=$userid"
            val call = ActService.sch_add(dynamicUrl, Sch_Request(name,i_startHour,i_endHour,i_dayOfWeek)) // 시작,끝시간, 요일 모두 인트인데 스트링으로 넣어줬다.
            call.enqueue(object : Callback<JoinResponse> {
                override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
                    val serverResponse = response.body()
                    Toast.makeText(applicationContext, "등록 성공", Toast.LENGTH_SHORT).show()
                    Log.d("sch_Result", "Response: $serverResponse")
                }

                override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "등록 실패", Toast.LENGTH_SHORT).show()
                    Log.e("sch_register", "Failed to send request to server. Error: ${t.message}")
                }
            })

            val resultIntent = Intent().apply {
                putExtra("name", name)
                putExtra("startHour", startHour)
                putExtra("endHour", endHour)
                putExtra("dayOfWeek", dayOfWeek)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}