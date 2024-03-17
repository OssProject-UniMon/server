package com.example.why1.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.why1.R
import com.example.why1.databinding.ActivityJoinBinding


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

        //회원가입 버튼 원클릭
        binding.registerBtn.setOnClickListener {
            //값 받아오기, 총 스트링 8개
            val nickname = binding.nickname1.text.toString() //아이디(닉네임)
            val email = binding.inputemail.text.toString() // 이메일
            val password = binding.password2.text.toString() // 비밀번호
            val re_pass = binding.rePassword.text.toString() // 비번확인
            val selected_gender = binding.rGroup.checkedRadioButtonId
            val gender_semi = findViewById<RadioButton>(selected_gender)
            val gender = gender_semi.text.toString() // 성별선택값
            val scholar = "동국대 인재 장학금(임시값)"
            val major = binding.major.text.toString() //전공
            System.out.println(major)

            //case1 비밀번호 틀림
            if(re_pass != password){
                Toast.makeText(applicationContext, "비밀번호를 다시한번 확인하세요", Toast.LENGTH_SHORT).show()
            }
            servercode = Readservercode(nickname,email,password,gender,scholar,major,sp1_Result,sp2_Result)

            //case2 값 미입력
            if (servercode == 0){
                Toast.makeText(applicationContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
            else if (servercode == 1){
                Toast.makeText(applicationContext, "회원가입 성공!!", Toast.LENGTH_SHORT).show()
            }

        }

    }
    fun Readservercode(name: String, email: String, pass: String, gender: String, scholar: String, major: String, rich: String, home: String): Int {


        return 0
    }
}