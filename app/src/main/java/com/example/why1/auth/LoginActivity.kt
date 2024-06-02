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
import com.example.why1.appdata.AppData
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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var servercode = 0

        val editemail = findViewById<EditText>(R.id.email1)
        val editpass = findViewById<EditText>(R.id.password1)
        val loginbtn = findViewById<Button>(R.id.login1)

        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://43.202.82.18:443") //secure무시, 리트로핏 통신까지

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
                    val userid = response.body()?.userId
                    val logresult = response.body()
                    if (userid != null) {
                        AppData.S_userId = userid // 유저 아이디 공용변수에 업데이트
                    }
                    Log.d("LoginResult", "Response: $logresult")
                    System.out.println(result)
                    if (result != null && result == 1) {
                        servercode = result
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        Toast.makeText(applicationContext, "로그인 성공!", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(applicationContext, "이메일과 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LoginError", "Error: ${t.message}")
                }
            })
            System.out.println(servercode)
        }
    }

}