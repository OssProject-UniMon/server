package com.example.why1.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.why1.MainActivity
import com.example.why1.R

class LoginActivity : AppCompatActivity() {

    private var servercode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editemail = findViewById<EditText>(R.id.email1)
        val editpass = findViewById<EditText>(R.id.password1)
        val loginbtn = findViewById<Button>(R.id.login1)

        loginbtn.setOnClickListener {
            val inputemail = editemail.text.toString()
            val inputpass = editpass.text.toString()
            System.out.println(inputemail)
            System.out.println(inputpass)
            ReadServerCode(inputemail,inputpass)

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