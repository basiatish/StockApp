package com.example.stocks.ui.chart.linechart

import android.content.Context
import android.widget.TextView
import com.example.stocks.R
import com.example.stocks.models.remote.StockChart
import com.example.stocks.models.remote.StockDailyPriceHeader
import com.example.stocks.models.remote.StockYearlyPriceHeader
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*


class MarkerView(context: Context?, layoutResource: Int, private val price: List<StockChart>,
                 private val priceDaily: List<StockDailyPriceHeader>,
                 private val priceYearly: List<StockYearlyPriceHeader>,
                 private val range: String) : MarkerView(context, layoutResource) {

    private var markerPrice: TextView = findViewById(R.id.priceContent)
    private var markerDate: TextView = findViewById(R.id.dateContent)
    private var offSet2 = MPPointF()

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val xIndex = e?.x
        if (range == "1day" || range == "1week") {
            val data = priceDaily[0].historical.reversed()[(xIndex)?.toInt()!!]
            markerDate.text = "Date: ${dateConverterFullChart(data.date!!)}"
            markerPrice.text = "Price: ${String.format("%.2f", data.close).toDouble()}"
        } else if (range == "all") {
            val data = priceYearly[0].historical.reversed()[(xIndex)?.toInt()!!]
            markerDate.text = "Date: ${dateConverterFullChart(data.date!!)}"
            markerPrice.text = "Price: ${String.format("%.2f", data.close).toDouble()}"
        } else {
            val data = price[(xIndex)?.toInt()!!]
            markerDate.text = "Date: ${dateConverter(data.date!!)}"
            markerPrice.text = "Price: ${String.format("%.2f", data.close).toDouble()}"
        }
        super.refreshContent(e, highlight)
    }

    private fun dateConverterFullChart(data: String): String {
        return data
    }

    private fun dateConverter(data: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault())
        val fullDate = input.parse(data)
        return output.format(fullDate)
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        if (posX > width / 2) {
            offSet2 = MPPointF(-(getWidth()).toFloat(), -posY)
        } else {
            offSet2 = MPPointF(-(getWidth() / 4f), -posY)
        }

        return offSet2
    }
}