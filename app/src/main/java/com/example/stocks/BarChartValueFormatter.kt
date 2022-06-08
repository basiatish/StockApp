package com.example.stocks

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class BarChartValueFormatter(data: MutableList<StockDividendsHeader>) : ValueFormatter() {
    private val dataSet = data[0].historical.reversed()

    override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
        return dataSet[value.toInt()].label
    }

}