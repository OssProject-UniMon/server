package com.example.why1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.why1.auth.IntroActivity

class splashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //화면 3초뒤에 설정한 엑티비티로 넘어가게 하는 코드
        Handler().postDelayed(
            {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }, 3000)
    }
}