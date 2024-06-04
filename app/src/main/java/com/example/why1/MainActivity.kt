package com.example.why1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.why1.appdata.AppData
import com.example.why1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val nickname = AppData.user_nick

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.nicktext.text = nickname+"님 환영합니다!!"

        binding.btn3.setOnClickListener {
            val intent = Intent(this, moneyActivity::class.java)
            startActivity(intent)
        }

        binding.detailButtongo.setOnClickListener {
            val intent = Intent(this, ScholarActivity::class.java)
            startActivity(intent)
        }
    }
}