package com.example.why1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.why1.appdata.AccountData
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.why1.appdata.Accountadapter
import com.example.why1.auth.NetworkConnection
import com.example.why1.retropit.JoinResponse
import com.example.why1.retropit.ManageService
import com.example.why1.retropit.act_RegisterRequest
import com.example.why1.retropit.act_listResponse
import com.example.why1.appdata.AppData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class CalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cal)

        val userId = AppData.S_userId
        var accountList = arrayListOf<AccountData>()

        // secure무시, 리트로핏 통신까지
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://43.202.82.18:443")
        val actService = retrofit.create(ManageService::class.java)
        val dynamicUrl2 = "/account/logs?userId=$userId"
        val call = actService.act_list(dynamicUrl2)
        call.enqueue(object : Callback<act_listResponse> {
            override fun onResponse(call: Call<act_listResponse>, response: Response<act_listResponse>) {
                val logs = response.body()?.logList
                Log.d("moneyResult: ", "Response: $logs")

                logs?.forEach { log ->
                    val depositOrWithdraw = if (log.deposit != "0") "+" + log.deposit + "원" else "-" + log.withdraw + "원"
                    val formattedDate = parseDate(log.date)
                    val accountData = AccountData(
                        log.category, // 분류 태그 (여기에 카테고리가 와야함)
                        depositOrWithdraw, // 출금 or 입금 금액
                        formattedDate, // 날짜
                        log.balance, // 잔액
                        log.useStoreName // 상호명
                    )
                    accountList.add(accountData)
                }

                // 배열에 잘 들어갔는지 확인하는 로그 출력
                Log.d("accountList", "Size: ${accountList.size}")
                accountList.forEachIndexed { index, accountData ->
                    Log.d("accountList", "Item $index: $accountData")
                }

                // 카테고리별 지출 비율 계산 및 결과 표시
                displayCategoryPercentage(accountList)
            }

            override fun onFailure(call: Call<act_listResponse>, t: Throwable) {
                Log.e("act_showlist", "Failed to send request to server. Error: ${t.message}")
            }
        })
    }

    private fun parseDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: ParseException) {
            Log.e("DateParseError", "Failed to parse date: $dateString")
            dateString
        }
    }

    private fun displayCategoryPercentage(accountList: ArrayList<AccountData>) {
        val categoryTotals = mutableMapOf<String, Int>()
        var totalSpent = 0

        // 카테고리별 지출 합산
        accountList.forEach { account ->
            if (account.price.startsWith("-")) {
                val amount = account.price.replace("-", "").replace("원", "").toInt()
                val category = account.tag ?: "Unknown"
                categoryTotals[category] = (categoryTotals[category] ?: 0) + amount
                totalSpent += amount
            }
        }

        // UI 업데이트
        val resultContainer: LinearLayout = findViewById(R.id.resultContainer)
        resultContainer.removeAllViews() // 기존 뷰 제거

        categoryTotals.forEach { (category, amount) ->
            val percentage = (amount.toDouble() / totalSpent * 100).roundToInt()

            val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
            progressBar.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
            progressBar.max = 100
            progressBar.progress = percentage

            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.text = "$category: $percentage% - ${amount}원"

            resultContainer.addView(textView)
            resultContainer.addView(progressBar)
        }
    }
}