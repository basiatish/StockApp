package com.example.stocks.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocks.BuildConfig
import com.example.stocks.StockApi
import com.example.stocks.models.remote.*
import com.example.stocks.utils.network.StockApiStatus
import com.tradingview.lightweightcharts.api.series.common.SeriesData
import com.tradingview.lightweightcharts.api.series.models.LineData
import com.tradingview.lightweightcharts.api.series.models.Time
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class StockChartViewModel: ViewModel() {

    private val apiKey = BuildConfig.API_KEY

    private val _status = MutableLiveData<StockApiStatus>()

    val status: LiveData<StockApiStatus> = _status

    private val _priceChart = MutableLiveData<MutableList<StockChart>>()

    val priceChart: LiveData<MutableList<StockChart>> = _priceChart

    private val _priceChartDaily = MutableLiveData<MutableList<StockDailyPriceHeader>>()

    val priceChartDaily: LiveData<MutableList<StockDailyPriceHeader>> = _priceChartDaily

    private var _priceChartYearly = MutableLiveData<MutableList<StockYearlyPriceHeader>>()

    val priceChartYearly: LiveData<MutableList<StockYearlyPriceHeader>> = _priceChartYearly

    var priceDaily: MutableList<StockDailyPriceHeader>? = mutableListOf()

    fun getChartData(time: String, compName: String) {
        viewModelScope.launch(IO) {
            withContext(Main) {
                _status.value = StockApiStatus.LOADING
            }
            try {
                if (time == "1day" || time == "1week") {
                    priceDaily = mutableListOf(
                        StockApi.retrofitService
                            .getFullHistoryDailyPrice(compName, apiKey))
                    setupChartData()
//                } else if (time == "all") {
//                    _priceChartYearly.value = mutableListOf(
//                        StockApi.retrofitService
//                            .getFullHistoryPrice(compName, "line", apiKey))
//                } else {
//                    _priceChart.value = StockApi.retrofitService
//                        .getChart(time, compName, apiKey)
                }
                withContext(Main) {
                    _status.value = StockApiStatus.DONE
                }
            } catch (e: Exception) {
                withContext(Main) {
                    _status.value = StockApiStatus.ERROR
                }
                priceDaily = mutableListOf()
                println(e.message)
            }
        }
    }

//    fun getPointDate(point: Int): String {
//        return priceDaily!![0].historical
//    }

//    fun getChartData(time: String, compName: String) {
//        viewModelScope.launch(IO) {
//            _status.value = StockApiStatus.LOADING
//            try {
//                if (time == "1day" || time == "1week") {
//                    _priceChartDaily.value = mutableListOf(
//                        StockApi.retrofitService
//                            .getFullHistoryDailyPrice(compName, apiKey))
//                } else if (time == "all") {
//                    _priceChartYearly.value = mutableListOf(
//                        StockApi.retrofitService
//                            .getFullHistoryPrice(compName, "line", apiKey))
//                } else {
//                    _priceChart.value = StockApi.retrofitService
//                        .getChart(time, compName, apiKey)
//                }
//                withContext(Main) {
//                    _status.value = StockApiStatus.DONE
//                }
//            } catch (e: Exception) {
//                withContext(Main) {
//                    _status.value = StockApiStatus.ERROR
//                }
//                _priceChart.value = mutableListOf()
//                println(e.message)
//            }
//        }
//    }

    fun getChartData(): MutableList<SeriesData> {
        return data
    }

    fun getLastChartPoint(): MutableMap<String, String> {
        val lastBar = mutableMapOf<String, String>()
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.
        parse(priceDaily?.get(0)?.historical?.first()?.date.toString(), format)
        lastBar["date"] = "${date.dayOfMonth} - ${date.monthValue} - ${date.year}"
        lastBar["price"] = (((priceDaily?.get(0)?.historical?.first()?.close)!! * 100.0).
        roundToInt() / 100.0).toString()
        return lastBar
    }

    val data: MutableList<SeriesData> = mutableListOf()

    private fun setupChartData() {

        val price = priceDaily!!

        for (item in price[0].historical.reversed()) {
            data.add(LineData(Time.StringTime(item.date!!), item.close!!))
        }


//        return mutableListOf(
//            LineData(Time.StringTime("2022-08-01"), 180.24.toFloat()),
//            LineData(Time.StringTime("2022-08-02"), 144.56.toFloat()),
//            LineData(Time.StringTime("2022-08-03"), 133.88.toFloat()),
//            LineData(Time.StringTime("2022-08-04"), 154.43.toFloat()),
//            LineData(Time.StringTime("2022-08-05"), 163.04.toFloat()),
//            LineData(Time.StringTime("2022-08-08"), 164.87.toFloat()),
//            LineData(Time.StringTime("2022-08-09"), 164.92.toFloat()),
//            LineData(Time.StringTime("2022-08-10"), 169.24.toFloat())
//        )
    }

}