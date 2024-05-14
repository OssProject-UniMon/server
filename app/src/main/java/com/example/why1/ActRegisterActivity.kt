package com.example.why1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.why1.databinding.ActivityActRegisterBinding
import com.example.why1.appdata.AppData
import com.example.why1.auth.NetworkConnection
import com.example.why1.retropit.JoinResponse
import com.example.why1.retropit.ManageService
import com.example.why1.retropit.act_RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActRegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityActRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_register)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_act_register) //레이아웃 데이터 바인딩

        //secure무시, 리트로핏 통신까지
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://52.79.154.36:443")
        val ActService = retrofit.create(ManageService::class.java)

        val userid = AppData.S_userId //유저 아이디 불러오기
        var sp1_Result = ""

        //뱅크 스피너
        val moneyArray = resources.getStringArray(R.array.bank_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moneyArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBank.adapter = adapter
        binding.spinnerBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택된 항목에 대한 처리
                sp1_Result = moneyArray[position]
                Toast.makeText(applicationContext, "(테스트)선택된 항목: $sp1_Result", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때의 처리
            }
        }

        binding.registerAct.setOnClickListener {

            //계좌 연동하기, 일단 임시로 리퀘스트값 채웠음,일단 페이지 들어오면 바로 실행되게함 ,나중에 변수로 바꿔
            val userId = 1 // 임시값, 유저 id 인트래
            val dynamicUrl = "/account/account-regist?userId=$userId"
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
            AppData.Access_code = 1 //등록되었을때 공용변수 업데이트, 나중에 등록성공했을때로 코드 올리셈

            val intent = Intent(this@ActRegisterActivity, moneyActivity::class.java)
            startActivity(intent)
        }

    }
}