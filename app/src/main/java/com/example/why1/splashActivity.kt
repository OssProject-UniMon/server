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
        val admin_code = 0

        //화면 3초뒤에 설정한 엑티비티로 넘어가게 하는 코드
        if(admin_code == 0) {
            Handler().postDelayed(
                {
                    startActivity(Intent(this, IntroActivity::class.java))
                    finish()
                }, 3000
            )
        }
        else {
            Handler().postDelayed(
                {
                    startActivity(Intent(this, ChartActivity::class.java))
                    finish()
                }, 3000
            )
        }
    }
}