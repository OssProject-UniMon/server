package com.example.why1

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.why1.appdata.AppData
import com.example.why1.appdata.Schedule
import com.example.why1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var scheduleGrid: GridLayout
    private val schedules = mutableListOf<Schedule>()
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

        scheduleGrid = findViewById(R.id.scheduleGrid)
        initializeGrid()

        val addScheduleButton = findViewById<Button>(R.id.addScheduleButton)
        addScheduleButton.setOnClickListener {
            val intent = Intent(this, AddScheduleActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_SCHEDULE)
        }

    }

    private fun initializeGrid() {
        // Create first row with days of the week
        val days = listOf("시간/요일", "월", "화", "수", "목", "금", "토", "일")
        for (i in days.indices) {
            addTextToGrid(days[i], i, 0, true)
        }

        // Create first column with hours
        for (i in 6..24) {
            addTextToGrid("${i}:00", 0, i - 5, true)
        }

        // Initialize empty cells
        for (i in 1..7) {
            for (j in 1..19) {
                addTextToGrid("", i, j, false)
            }
        }
    }

    private fun addTextToGrid(text: String, col: Int, row: Int, header: Boolean) {
        val textView = TextView(this).apply {
            this.text = text
            if (header) {
                setBackgroundColor(Color.LTGRAY)
            } else {
                setBackgroundColor(Color.WHITE)
            }
            setPadding(8, 8, 8, 8)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(col, 1f)
                rowSpec = GridLayout.spec(row)
            }
        }
        scheduleGrid.addView(textView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_SCHEDULE && resultCode == Activity.RESULT_OK) {
            val name = data?.getStringExtra("name")
            val startHour = data?.getIntExtra("startHour", 0)
            val endHour = data?.getIntExtra("endHour", 0)
            val dayOfWeek = data?.getIntExtra("dayOfWeek", 0)

            if (name != null && startHour != null && endHour != null && dayOfWeek != null) {
                val schedule = Schedule(name, startHour, endHour, dayOfWeek)
                schedules.add(schedule)
                updateScheduleGrid(schedule)
            }
        }
    }

    private fun updateScheduleGrid(schedule: Schedule) {
        for (hour in schedule.startHour until schedule.endHour) {
            val col = schedule.dayOfWeek
            val row = hour - 5 // 6시에 해당하는 행은 1부터 시작하므로 6을 빼줌
            if (col in 1..7 && row in 1..19) {
                val textView = TextView(this).apply {
                    text = schedule.name
                    setBackgroundColor(Color.parseColor("#FFDDC1"))
                    setPadding(8, 8, 8, 8)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(col, 1f)
                        rowSpec = GridLayout.spec(row)
                    }
                }
                scheduleGrid.addView(textView)
            }
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_SCHEDULE = 1
    }

}