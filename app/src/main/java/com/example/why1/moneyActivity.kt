package com.example.why1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class moneyActivity : AppCompatActivity() {

    var accountList = arrayListOf<AccountData>() //데이터 클래스를 배열에 넣어줌

    var sampleData1 = AccountData("음식:식비","2만원","4/3","#체크카드","동국대소비점")
    var sampleData2 = AccountData("음식:식비","1만원","4/4","#계좌이체","유승원")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money)

        accountList.add(sampleData1)
        accountList.add(sampleData2) // 테스트용 계좌연동 예시 데이터

        var mainlistview = findViewById<ListView>(R.id.moneyListView)
        val accountadapter = Accountadapter(this, accountList)
        mainlistview.adapter = accountadapter
    }
}