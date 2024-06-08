package com.example.why1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val startHourEditText = findViewById<EditText>(R.id.startHourEditText)
        val endHourEditText = findViewById<EditText>(R.id.endHourEditText)
        val dayOfWeekEditText = findViewById<EditText>(R.id.dayOfWeekEditText)
        val addButton = findViewById<Button>(R.id.addButton)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val startHour = startHourEditText.text.toString().toInt()
            val endHour = endHourEditText.text.toString().toInt()
            val dayOfWeek = dayOfWeekEditText.text.toString().toInt()

            val resultIntent = Intent().apply {
                putExtra("name", name)
                putExtra("startHour", startHour)
                putExtra("endHour", endHour)
                putExtra("dayOfWeek", dayOfWeek)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}