package com.example.why1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.why1.appdata.AccountData
import com.example.why1.appdata.Accountadapter
import com.example.why1.auth.NetworkConnection
import com.example.why1.retropit.JoinResponse
import com.example.why1.retropit.ManageService
import com.example.why1.retropit.act_RegisterRequest
import com.example.why1.retropit.act_listResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class moneyActivity : AppCompatActivity() {

    var access_code = 0 //계좌 연동 확인용 변수
    var accountList = arrayListOf<AccountData>() //데이터 클래스를 배열에 넣어줌

    var sampleData1 = AccountData("음식:식비","2만원","4/3","#체크카드","동국대소비점")
    var sampleData2 = AccountData("음식:식비","1만원","4/4","#계좌이체","유승원")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money)

        accountList.add(sampleData1)
        accountList.add(sampleData2) // 테스트용 계좌연동 예시 데이터

        //레아이웃 처리
        val mainlistview = findViewById<ListView>(R.id.moneyListView)
        val accountadapter = Accountadapter(this, accountList)
        mainlistview.adapter = accountadapter
        val act_btn = findViewById<Button>(R.id.btn_account_str)
        val mymoney = findViewById<TextView>(R.id.text1)

        //secure무시, 리트로핏 통신까지
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://54.180.150.195:443")
        val ActService = retrofit.create(ManageService::class.java)

        //계좌 연동하기, 일단 임시로 리퀘스트값 채웠음,일단 페이지 들어오면 바로 실행되게함 ,나중에 변수로 바꿔
        val userId = 1 // 임시값, 유저 id 인트래
        val dynamicUrl = "/account/regist?userId=$userId"
        val call = ActService.act_register(dynamicUrl, act_RegisterRequest("KB","p","110500411959", "1234","abc1234","wal1234","000127"))
        call.enqueue(object : Callback<JoinResponse> {
            override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
                val serverResponse = response.body()
                Toast.makeText(applicationContext, "등록 성공", Toast.LENGTH_SHORT).show()
                Log.d("account_Result", "Response: $serverResponse")
            }

            override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "등록 실패", Toast.LENGTH_SHORT).show()
                Log.e("act_register", "Failed to send request to server. Error: ${t.message}")
            }
        })

        //임시로 버튼 누르면 값 가져오게함
        act_btn.setOnClickListener {
            val dynamicUrl2 = "/account/logs?userId=$userId"
            val call = ActService.act_list(dynamicUrl2,"20240401","20240405")
            call.enqueue(object : Callback<act_listResponse> {
                override fun onResponse(call: Call<act_listResponse>, response: Response<act_listResponse>) {
                    val logresult = response.body()
                    Log.d("Result: ", "Response: $logresult")
                }

                override fun onFailure(call: Call<act_listResponse>, t: Throwable) {

                    Log.e("act_showlist", "Failed to send request to server. Error: ${t.message}")
                }
            })

            val intent = Intent(this@moneyActivity, ActRegisterActivity::class.java)
            startActivity(intent)
        }



        //통신 성공하면 연결 버튼 사라지게 하는거,나중에 서버통신 리스폰스에 넣어주면 될듯?
        if (access_code == 0) {
            act_btn.visibility = View.VISIBLE
            mymoney.visibility = View.GONE
        } else if (access_code == 1) {
            // code가 1이면 버튼을 숨기고 텍스트뷰를 보여줌
            act_btn.visibility = View.GONE
            mymoney.visibility = View.VISIBLE
        }



    }
}