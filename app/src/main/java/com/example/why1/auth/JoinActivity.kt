package com.example.why1.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.why1.R
import com.example.why1.databinding.ActivityJoinBinding
import com.example.why1.retropit.EmailResponse
import com.example.why1.retropit.JoinRequest
import com.example.why1.retropit.JoinResponse
import com.example.why1.retropit.JoinService
import com.example.why1.retropit.ManageService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import com.example.why1.auth.NetworkConnection


class JoinActivity : AppCompatActivity() {

    private lateinit var binding : ActivityJoinBinding
    var servercode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_join)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_join) //레이아웃 데이터 바인딩


        //아래 코드는 바인딩 없이 짰음 , 드래그드롭 애들만
        val spinner1 = findViewById<Spinner>(R.id.spinner1)
        val spinner2 = findViewById<Spinner>(R.id.spinner2)
        var sp1_Result = "wal"
        var sp2_Result = "wal"

        //첫번째 스피너
        val moneyArray = resources.getStringArray(R.array.my_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moneyArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택된 항목에 대한 처리
                sp1_Result = moneyArray[position]
                Toast.makeText(applicationContext, "(테스트)선택된 항목: $sp1_Result", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때의 처리
            }
        }

        //두번째 스피너
        val placeArray = resources.getStringArray(R.array.place_array)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, placeArray)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택된 항목에 대한 처리
                sp2_Result = placeArray[position]
                System.out.println(sp2_Result)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때의 처리
            }
        }

        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://54.180.150.195:443") //secure무시, 리트로핏 통신까지
        val JoinService = retrofit.create(JoinService::class.java)
        val Allservice = retrofit.create(ManageService::class.java)

        //회원가입 버튼 원클릭
        binding.registerBtn.setOnClickListener {
            //값 받아오기, 총 스트링 8개
            val nickname = binding.nickname1.text.toString() //아이디(닉네임)
            val email = binding.inputemail.text.toString() // 이메일
            val password = binding.password2.text.toString() // 비밀번호
            val re_pass = binding.rePassword.text.toString() // 비번확인
            val selected_gender = binding.rGroup.checkedRadioButtonId
            val gender_semi = findViewById<RadioButton>(selected_gender)
            val gender = gender_semi.text.toString() // 성별 선택 값
            val scholar = "동국대 인재 장학금(임시값)" // 0,1로 선택하게 해야함! 일단 선택x 임시값 보내게 해둘게
            val major = binding.major.text.toString() //전공
            val grade = 3.5 //임시값 ... 추가 시켜야함
            System.out.println(major)

            //case1 비밀번호 틀림
            if(re_pass != password){
                Toast.makeText(applicationContext, "비밀번호를 다시한번 확인하세요", Toast.LENGTH_SHORT).show()
            }
            else {
                val call = JoinService.Register(
                    JoinRequest(
                        email,
                        password,
                        nickname,
                        major,
                        grade,
                        gender,
                        sp1_Result,
                        1, //임시값
                        sp2_Result
                    )
                )
                call.enqueue(object : Callback<JoinResponse> {
                    override fun onResponse(
                        call: Call<JoinResponse>,
                        response: Response<JoinResponse>
                    ) {
                        val result = response.body()?.serverCode
                        if (result != null && result == 1) {
                            servercode = result //인트값 변환
                            Toast.makeText(applicationContext, "회원가입 성공!!", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(applicationContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        }
                        Log.d("joinResult", "Response: $result")
                        System.out.println(result)
                    }

                    override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                        Log.e("joinError", "Error: ${t.message}")
                    }
                })
            }

        }

        binding.btn2.setOnClickListener {
            val email = binding.inputemail.text.toString() // 이메일
            val dynamicUrl = "/user/$email/check" // 동적으로 생성된 URL

            val call = Allservice.emailresponse(dynamicUrl, email)
            call.enqueue(object : Callback<EmailResponse> {
                override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                    if (response.isSuccessful) {
                        val serverResponse = response.body()
                        serverResponse?.let {
                            val serverCode = it.serverCode
                            Log.d("ServerCode", "Received server code: $serverCode")
                            // 여기에서 서버코드에 따른 처리를 수행
                        }
                    } else {
                        // 서버 응답이 실패한 경우
                        Log.e("ServerResponse", "Failed to get server code. Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                    // 네트워크 요청 자체가 실패한 경우
                    Log.e("ServerRequest", "Failed to send request to server. Error: ${t.message}")
                }
            })
        }

    }


    fun Readservercode(name: String, email: String, pass: String, gender: String, scholar: String, major: String, rich: String, home: String): Int {


        return 0
    }
}