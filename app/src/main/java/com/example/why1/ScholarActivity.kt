package com.example.why1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.why1.appdata.NoDividerItemDecoration
import com.example.why1.appdata.S_adapter
import com.example.why1.appdata.S_data

class ScholarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scholar)

        val recyclerView: RecyclerView = findViewById(R.id.resultview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = S_adapter(getData())

        // Divider 제거
        recyclerView.addItemDecoration(NoDividerItemDecoration(this))
    }

    private fun getData(): List<S_data> {
        return listOf(
            S_data("Title 1", "2024-06-01", "Content 1",arrayOf("지원대상1", "지원내용1", "신청기간1", "신청방법1")),
            S_data("Title 2", "2024-06-02", "Content 2",arrayOf("지원대상1", "지원내용1", "신청기간1", "신청방법1")),
            S_data("Title 3", "2024-06-03", "Content 3",arrayOf("지원대상1", "지원내용1", "신청기간1", "신청방법1"))
        )
    }
}