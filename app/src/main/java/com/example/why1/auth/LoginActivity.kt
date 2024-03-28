package com.example.why1.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.why1.MainActivity
import com.example.why1.R
import com.example.why1.retropit.LoginRequest
import com.example.why1.retropit.LoginResponse
import com.example.why1.retropit.ManageService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private var servercode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editemail = findViewById<EditText>(R.id.email1)
        val editpass = findViewById<EditText>(R.id.password1)
        val loginbtn = findViewById<Button>(R.id.login1)

        //리트로핏 서버통신 구현
        val retrofit = Retrofit.Builder()
            .baseUrl("https://3030-210-94-220-228.ngrok-free.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val TestService = retrofit.create(ManageService::class.java)

        loginbtn.setOnClickListener {
            val inputemail = editemail.text.toString()
            val inputpass = editpass.text.toString()
            System.out.println(inputemail)
            System.out.println(inputpass)
            //ReadServerCode(inputemail,inputpass) //테스트용

            val call = TestService.login(LoginRequest(inputemail, inputpass))
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val result = response.body()?.serverCode
                    Log.d("LoginResult", "Response: $result")
                    System.out.println(result)
                    if (result != null) {
                        servercode = result
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LoginError", "Error: ${t.message}")
                }
            })

            if (servercode == 1){
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(applicationContext, "로그인 성공!", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
            else {
                Toast.makeText(applicationContext, "이메일과 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun ReadServerCode(email : String , password : String ):Int {
        // 서버코드를 랜덤하게 계산 ... 테스트용
        val random = (0..1).random()
        servercode = random
        return servercode
    }
}