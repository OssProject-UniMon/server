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
import com.example.why1.appdata.AppData
import com.example.why1.auth.NetworkConnection
import com.example.why1.databinding.ActivityCardRegisterBinding
import com.example.why1.retropit.JoinResponse
import com.example.why1.retropit.ManageService
import com.example.why1.retropit.act_RegisterRequest
import com.example.why1.retropit.card_RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CardRegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCardRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_register)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_card_register) //레이아웃 데이터 바인딩

        //secure무시, 리트로핏 통신까지
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://5231-112-171-58-99.ngrok-free.app")
        val ActService = retrofit.create(ManageService::class.java)

        val userid = AppData.S_userId //유저 아이디 불러오기
        var sp1_Result = ""

        //카드 스피너
        val moneyArray = resources.getStringArray(R.array.card_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moneyArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCard.adapter = adapter
        binding.spinnerCard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택된 항목에 대한 처리
                sp1_Result = moneyArray[position]
                Toast.makeText(applicationContext, "(테스트)선택된 항목: $sp1_Result", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때의 처리
            }
        }

        binding.registerCard.setOnClickListener {

            //레이아웃에서 값 받아오기
            val cardnum = binding.cardNum.text.toString()
            val c_webid = binding.cardId.text.toString()
            val c_webpw = binding.cardPw.text.toString()

            //카드 연동하기
            val dynamicUrl = "/account/card-regist?userId=$userid"
            val call = ActService.card_register(dynamicUrl, card_RegisterRequest("SHINHAN","P","5107376798062092", "woalsdl7399","driermine7399!"))
            call.enqueue(object : Callback<JoinResponse> {
                override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
                    val serverResponse = response.body()
                    Toast.makeText(applicationContext, "등록 성공", Toast.LENGTH_SHORT).show()
                    Log.d("card_Result", "Response: $serverResponse")
                }

                override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "등록 실패", Toast.LENGTH_SHORT).show()
                    Log.e("card_register", "Failed to send request to server. Error: ${t.message}")
                }
            })
            AppData.Access_code = 1 //등록되었을때 공용변수 업데이트, 나중에 등록성공했을때로 코드 올리셈

            val intent = Intent(this@CardRegisterActivity, moneyActivity::class.java)
            startActivity(intent)
        }

        }


    }
