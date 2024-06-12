package com.example.why1

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val content = intent.getStringExtra("content")
        val details = intent.getStringArrayExtra("details")

        val titleTextView: TextView = findViewById(R.id.titleTextView)
        val dateTextView: TextView = findViewById(R.id.dateTextView)
        val contentTextView: TextView = findViewById(R.id.contentTextView)
        val targetTextView: TextView = findViewById(R.id.targetTextView)
        val descriptionTextView: TextView = findViewById(R.id.descriptionTextView)
        val periodTextView: TextView = findViewById(R.id.periodTextView)
        val methodTextView: TextView = findViewById(R.id.methodTextView)

        titleTextView.text = title
        dateTextView.text = date
        contentTextView.text = content
        details?.let {
            targetTextView.text = it[0]
            descriptionTextView.text = it[1]
            periodTextView.text = it[2]
            methodTextView.text = it[3]
        }

        periodTextView.setOnClickListener {
            val url = periodTextView.text.toString()
            if (url.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }

}