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
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://43.202.82.18:443")
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
                sp1_Result = moneyArray[position] //은행 종류(bank)
                Toast.makeText(applicationContext, "(테스트)선택된 항목: $sp1_Result", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때의 처리
            }
        }

        binding.registerAct.setOnClickListener {

            //레이아웃에서 값 가져오기
            val actnum = binding.actNum.text.toString()
            val back_p = binding.passwordBank.text.toString()
            val fast_i = binding.fastId.text.toString()
            val fast_p = binding.fastPw.text.toString()
            val my_num = binding.myNum.text.toString()
            //값 가져온거 계좌번호만 앱데이터에 업데이트
            AppData.bank_num = actnum

            //계좌 연동하기, 일단 임시로 리퀘스트값 채웠음,버튼 누르면 서버통신 ,나중에 변수로 바꿔
            val dynamicUrl = "/account/account-regist?userId=$userid"
            val call = ActService.act_register(dynamicUrl, act_RegisterRequest("IBK","P","98003545601011", "4625","","","001031"))
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

            val intent = Intent(this@ActRegisterActivity, CardRegisterActivity::class.java)
            startActivity(intent)
        }

    }
}