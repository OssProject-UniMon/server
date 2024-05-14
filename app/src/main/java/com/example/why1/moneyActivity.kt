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
import com.example.why1.appdata.AppData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class moneyActivity : AppCompatActivity() {

    var accountList = arrayListOf<AccountData>() //데이터 클래스를 배열에 넣어줌

    var sampleData1 = AccountData("음식:식비","2만원","4/3","#체크카드","동국대소비점")
    var sampleData2 = AccountData("음식:식비","1만원","4/4","#계좌이체","유승원")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money)

        accountList.add(sampleData1)
        accountList.add(sampleData2) // 테스트용 계좌연동 예시 데이터
        val access_code = AppData.Access_code // 공용변수 엑세스 코드 불러오기
        val userId = AppData.S_userId // 공용변수 유저 아이디 불러오기
        Log.d("access_code", "code::Response: $access_code")
        Log.d("userId", "code::Response: $userId")

        //리스트 레아이웃 처리
        val mainlistview = findViewById<ListView>(R.id.moneyListView)
        val accountadapter = Accountadapter(this, accountList)
        mainlistview.adapter = accountadapter
        val act_btn = findViewById<Button>(R.id.btn_account_str)
        val mymoney = findViewById<TextView>(R.id.text1)

        //secure무시, 리트로핏 통신까지
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://52.79.154.36:443")
        val ActService = retrofit.create(ManageService::class.java)



        //엑티비티 실행 되자마자 값 가져오게함
        act_btn.setOnClickListener {
            val dynamicUrl2 = "/account/account-logs?userId=$userId"
            val call = ActService.act_list(dynamicUrl2,"20240401","20240405")
            call.enqueue(object : Callback<act_listResponse> {
                override fun onResponse(call: Call<act_listResponse>, response: Response<act_listResponse>) {
                    val logs = response.body()?.log_list
                    Log.d("Result: ", "Response: $logs")

                    var count = 1
                    logs?.forEach { log ->
                        val depositOrWithdraw = if (log.deposit != "0") log.deposit else log.withdraw
                        val accountData = AccountData(
                            "count$count",
                            depositOrWithdraw,
                            log.trans_dt,
                            log.trans_type,
                            log.trans_remark
                        )
                        accountList.add(accountData)
                        count++
                    }
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