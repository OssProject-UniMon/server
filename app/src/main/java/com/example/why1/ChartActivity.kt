package com.example.why1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ChartActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        barChart = findViewById(R.id.barChart)

        // 차트 데이터 설정
        setData()
        // 추가 설정
        customizeChart()
    }

    private fun setData() {
        // 예제 데이터 생성
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 30f))
        entries.add(BarEntry(2f, 50f))
        entries.add(BarEntry(3f, 40f))
        entries.add(BarEntry(4f, 80f))

        val barDataSet = BarDataSet(entries, "Balance Data")

        // 막대별 색상 설정
        barDataSet.colors = listOf(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)

        val barData = BarData(barDataSet)
        barChart.data = barData

        // 설명 제거 (선택사항)
        val description = Description()
        description.text = ""
        barChart.description = description

        // 차트 새로고침
        barChart.invalidate()
    }

    private fun customizeChart() {
        // X 축 설정
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("잡화", "카페", "쇼핑", "주류")) // 각 막대의 레이블 설정

        // Y 축 설정
        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f // 최소값 설정
        leftAxis.axisMaximum = 100f // 최대값 설정

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false // 오른쪽 축 비활성화
    }
}