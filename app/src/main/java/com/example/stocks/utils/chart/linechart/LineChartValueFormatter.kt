package com.example.stocks.utils.chart.linechart

import android.annotation.SuppressLint
import android.util.Log
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class LineChartValueFormatter(data: List<String>, time: String) : ValueFormatter() {
    private val dataSet = data
    private val range = time
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val date: String
        if (range == "1day" || range == "1week" || range == "1year" || range == "all") {
            date = dateConverterFullChart(value)
        } else {
            date = dateConverter(value)
        }
        return date
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateConverter(value: Float) : String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputDate = SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault())
            val fullDate = input.parse(dataSet[value.toInt()])
            val date = outputDate.format(fullDate)
            date
        } catch (e: Exception) {
            Log.e(e.message, "TAG")
            ""
        }
    }

    private fun dateConverterFullChart(value: Float) : String {
        return try {
            val date = dataSet[value.toInt()]
            date
        } catch (e: Exception) {
            Log.e(e.message, "TAG")
            ""
        }
    }

}