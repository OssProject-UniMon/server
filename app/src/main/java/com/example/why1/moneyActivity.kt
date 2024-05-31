package com.example.why1

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
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

    var sampleData1 = AccountData("쇼핑","-17090원","05.23","469,029원","네이버페이")
    var sampleData2 = AccountData("잡화,소매","-2400원","05.22","484,199원","GS25노고산점")
    var sampleData3 = AccountData("페이 지출","-11890원","05.22","486,519원","카카오페이")
    var sampleData4 = AccountData("잡화,소매","-1600원","05.22","498,409원","동국대학교소비자생")
    var sampleData5 = AccountData("잡화,소매","-2700원","05.22","500,009원","세븐일레븐 동국대신")
    var sampleData6 = AccountData("잡화,소매","-2900원","05.22","502,709원","씨유 신촌 노고산점")
    var sampleData7 = AccountData("잡화,소매","-4000원","05.21","505,609원","씨유 신촌 노고산점")
    var sampleData8 = AccountData("잡화,소매","-5700원","05.20","509,609원","GS25노고산점")
    var sampleData9 = AccountData("페이 지출","-10000원","05.20","515,309원","카카오페이")
    var sampleData10 = AccountData("잡화,소매","-4000원","05.20","525,309원","씨유 신촌 노고산점")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money)

        //accountList.add(sampleData1)
        //accountList.add(sampleData2)
        //accountList.add(sampleData3)
        //accountList.add(sampleData4)
        //accountList.add(sampleData5)
        //accountList.add(sampleData6)
        //accountList.add(sampleData7)
        //accountList.add(sampleData8)
        //accountList.add(sampleData9)
        //accountList.add(sampleData10)
        val access_code = AppData.Access_code // 공용변수 엑세스 코드 불러오기
        val userId = AppData.S_userId // 공용변수 유저 아이디 불러오기
        Log.d("access_code", "code::Response: $access_code")
        Log.d("userId", "code::Response: $userId")

        //리스트 레아이웃 처리
        //val mainlistview = findViewById<ListView>(R.id.moneyListView)
        //val accountadapter = Accountadapter(this, accountList)
        //mainlistview.adapter = accountadapter
        //레이아웃 처리
        val act_btn = findViewById<Button>(R.id.btn_account_str)
        val mymoney = findViewById<TextView>(R.id.text1)
        val myname = findViewById<TextView>(R.id.text2)
        val myact = findViewById<TextView>(R.id.text3)
        val new_btn = findViewById<Button>(R.id.renew)

        //스피너 처리
        var sp1_Result = ""
        val moneyArray = resources.getStringArray(R.array.setting_array)
        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_item, moneyArray) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                // Apply the custom layout for dropdown items
                (view as TextView).setTextColor(Color.BLACK)
                view.gravity = Gravity.CENTER
                return view
            }
        }
        val setting: Spinner = findViewById(R.id.spinner_setting)
        setting.adapter = adapter
        setting.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택된 항목에 대한 처리
                sp1_Result = moneyArray[position] // 세팅값 설정
                Toast.makeText(applicationContext, "선택 변경(단위): $sp1_Result", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때의 처리
            }
        }

        //secure무시, 리트로핏 통신까지
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://4b3e-112-171-58-99.ngrok-free.app")
        val ActService = retrofit.create(ManageService::class.java)


            val dynamicUrl2 = "/account/test?userId=$userId"
            val call = ActService.act_list(dynamicUrl2)
            call.enqueue(object : Callback<act_listResponse> {
                override fun onResponse(call: Call<act_listResponse>, response: Response<act_listResponse>) {
                    val logs = response.body()?.logList
                    Log.d("moneyResult: ", "Response: $logs")

                    var count = 1
                    logs?.forEach { log ->
                        val depositOrWithdraw = if (log.deposit != "0") "+"+log.deposit else "-"+log.withdraw
                        val accountData = AccountData(
                            log.category, //분류 태그 (여기에 카테고리가 와야함)
                            depositOrWithdraw, //출금or입금금액
                            log.date, // 날짜
                            log.balance, // !!여기를 밸런스(잔액)을 들어가게 하자!!
                            log.useStoreName // 상호명
                        )
                        accountList.add(accountData)
                        count++
                    }
                    // 배열에 잘 들어갔는지 확인하는 로그 출력
                    Log.d("accountList", "Size: ${accountList.size}")
                    accountList.forEachIndexed { index, accountData ->
                        Log.d("accountList", "Item $index: $accountData")}

                    val mainlistview = findViewById<ListView>(R.id.moneyListView)
                    val accountadapter = Accountadapter(this@moneyActivity, accountList)
                    mainlistview.adapter = accountadapter
                }

                override fun onFailure(call: Call<act_listResponse>, t: Throwable) {

                    Log.e("act_showlist", "Failed to send request to server. Error: ${t.message}")
                }
            })

        //계좌 연동하기 페이지 이동
        act_btn.setOnClickListener {

            val intent = Intent(this@moneyActivity, ActRegisterActivity::class.java)
            startActivity(intent)
        }



        //통신 성공하면 연결 버튼 사라지게 하는거
        if (access_code == 0) {
            act_btn.visibility = View.VISIBLE
            mymoney.visibility = View.GONE
            myact.visibility = View.GONE
            myname.visibility = View.GONE
            setting.visibility = View.GONE
            new_btn.visibility = View.GONE
        } else if (access_code == 1) {
            // code가 1이면 버튼을 숨기고 텍스트뷰를 보여줌
            act_btn.visibility = View.GONE
            mymoney.visibility = View.VISIBLE
            myact.visibility = View.VISIBLE
            myname.visibility = View.VISIBLE
            setting.visibility = View.VISIBLE
            new_btn.visibility = View.VISIBLE
            //myact.text = AppData.bank_num
        }



    }
}