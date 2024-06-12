package com.example.why1

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.why1.appdata.AppData
import com.example.why1.auth.NetworkConnection
import com.example.why1.retropit.AnsRequest
import com.example.why1.retropit.AnsResponse
import com.example.why1.retropit.JoinResponse
import com.example.why1.retropit.ManageService
import com.example.why1.retropit.act_RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private lateinit var chatLayout: LinearLayout
    private lateinit var inputEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var scrollView: ScrollView
    private var botAnswer = "안녕하세요! chat DPT 응답을 사용해주셔서 감사합니다!"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatLayout = findViewById(R.id.chatLayout)
        inputEditText = findViewById(R.id.inputEditText)
        sendButton = findViewById(R.id.sendButton)
        scrollView = findViewById(R.id.scrollView)
        val userId = AppData.S_userId

        //secure무시, 리트로핏 통신까지
        val okHttpClient = NetworkConnection.createOkHttpClient()
        val retrofit = NetworkConnection.createRetrofit(okHttpClient, "https://43.202.82.18:443")
        val ActService = retrofit.create(ManageService::class.java)

        val dynamicUrl2 = "/job/ready"
        val call2 = ActService.ready(dynamicUrl2)
        call2.enqueue(object : Callback<JoinResponse> {
            override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
                val serverResponse = response.body()
                Log.d("ans_Result", "Response: $serverResponse")
            }

            override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "연결 실패", Toast.LENGTH_SHORT).show()
                Log.e("ans_register", "Failed to send request to server. Error: ${t.message}")
            }
        })

        sendButton.setOnClickListener {
            val userInput = inputEditText.text.toString()
            addUserMessage(userInput)
            addBotMessage("답변 중...")
            val dynamicUrl = "/job/recommend?userId=$userId"
            val call = ActService.response_register(dynamicUrl, AnsRequest(userInput))
            call.enqueue(object : Callback<AnsResponse> {
                override fun onResponse(call: Call<AnsResponse>, response: Response<AnsResponse>) {
                    val serverResponse = response.body()?.responseMessage
                    Log.d("ans_Result", "Response: $serverResponse")
                    if (serverResponse != null) {
                        botAnswer = serverResponse
                    }
                    if (userInput.isNotBlank()) {
                        //addUserMessage(userInput)
                        addBotMessage(botAnswer)
                        inputEditText.text.clear()
                        scrollView.post {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                        }
                    }
                }

                override fun onFailure(call: Call<AnsResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "연결 실패", Toast.LENGTH_SHORT).show()
                    Log.e("ans_register", "Failed to send request to server. Error: ${t.message}")
                }
            })
        }
    }

    private fun addUserMessage(message: String) {
        val textView = TextView(this)
        textView.text = message
        textView.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
        textView.setTextColor(Color.BLACK)
        textView.textSize = 18f
        textView.background = ContextCompat.getDrawable(this, R.drawable.bubble_user)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 32, 0, 32) // 상하 마진 추가
        layoutParams.gravity = android.view.Gravity.END
        textView.layoutParams = layoutParams
        chatLayout.addView(textView)
    }

    private fun addBotMessage(message: String) {
        val textView = TextView(this)
        textView.text = message
        textView.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
        textView.setTextColor(Color.BLACK)
        textView.textSize = 18f
        textView.background = ContextCompat.getDrawable(this, R.drawable.bubble_user)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = android.view.Gravity.START
        layoutParams.setMargins(0, 32, 0, 32) // 상하 마진 추가
        textView.layoutParams = layoutParams
        chatLayout.addView(textView)
    }
}