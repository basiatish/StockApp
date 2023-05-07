package com.example.stocks.viewmodels

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocks.BuildConfig
import com.example.stocks.StockApi
import com.example.stocks.models.remote.*
import com.example.stocks.utils.network.StockStatus
import com.tradingview.lightweightcharts.api.chart.models.color.toIntColor
import com.tradingview.lightweightcharts.api.series.common.SeriesData
import com.tradingview.lightweightcharts.api.series.models.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter.ofPattern
import kotlin.math.roundToInt

class StockChartViewModel: ViewModel() {

    private val apiKey = BuildConfig.API_KEY

    private val _status = MutableLiveData<StockStatus>()
    val status: LiveData<StockStatus> = _status

    private val _dataStatus = MutableLiveData<StockStatus>()
    val dataStatus: LiveData<StockStatus> = _dataStatus

    private var _priceChartMH: MutableList<StockChart> = mutableListOf()
    val priceChartMH: MutableList<StockChart> = _priceChartMH

    private var _priceDaily: MutableList<StockDailyPriceHeader> = mutableListOf()
    val priceDaily: List<StockDailyPriceHeader> = _priceDaily

    var data: MutableList<SeriesData> = mutableListOf()

    fun loadChartData(time: String, compName: String) {
        viewModelScope.launch(IO) {
            withContext(Main) {
                _status.value = StockStatus.LOADING
            }
            try {
                if (time == "all") {
                    _priceDaily = mutableListOf(
                        StockApi.retrofitService
                            .getFullHistoryDailyPrice(compName, apiKey))
                } else {
                    _priceChartMH = StockApi.retrofitService
                        .getChart(time, compName, apiKey)
                }
                withContext(Main) {
                    _status.value = StockStatus.DONE
                }
            } catch (e: Exception) {
                withContext(Main) {
                    _status.value = StockStatus.ERROR
                }
                _priceDaily = mutableListOf()
                println(e.message)
            }
        }
    }

    fun getChartData(): MutableList<SeriesData> {
        return data
    }

    fun getLastChartPoint(range: String): MutableMap<String, String> {
        val lastBar = mutableMapOf<String, String>()
        if (range == "all") {
            val format = ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(_priceDaily[0].historical.reversed().last().date.toString(), format)
            val day = convertDate(date.dayOfMonth)
            val month = convertDate(date.monthValue)
            lastBar["date"] = "$day - $month - ${date.year}"
            lastBar["open"] = convertPrice(_priceDaily[0].historical.reversed().last().open!!)
            lastBar["price"] = convertPrice(_priceDaily[0].historical.reversed().last().close!!)
            lastBar["high"] = convertPrice(_priceDaily[0].historical.reversed().last().high!!)
            lastBar["low"] = convertPrice(_priceDaily[0].historical.reversed().last().low!!)
        } else {
            val format = ofPattern("yyyy-MM-dd HH:mm:ss")
            val date = LocalDateTime.parse(_priceChartMH.reversed().last().date, format)
            val hour = convertDate(date.hour)
            val minute = convertDate(date.minute)
            lastBar["date"] = "$hour:$minute"
            lastBar["open"] = convertPrice(_priceChartMH.reversed().last().open!!)
            lastBar["price"] = convertPrice(_priceChartMH.reversed().last().close!!)
            lastBar["high"] = convertPrice(_priceChartMH.reversed().last().high!!)
            lastBar["low"] = convertPrice(_priceChartMH.reversed().last().low!!)
        }
        return lastBar
    }

    fun getLastPrice(range: String): Int {
        return when(range) {
            "all" -> _priceDaily[0].historical.reversed().last().close!!.roundToInt()
            else -> _priceChartMH.reversed().last().close!!.roundToInt()
        }
    }

    fun convertPrice(priceValue: Float): String {
        return ((priceValue * 1000.0).roundToInt() / 1000.0).toString()
    }

    fun convertDate(dateValue: Int): String {
        return when {
            dateValue < 10 -> "0$dateValue"
            else -> dateValue.toString()
        }
    }

    fun createData(type: String, time: String) {
        data = mutableListOf()
        when (type) {
            "Line" -> createLineData(time)
            "Candle" -> createCandleData(time)
            "Bar" -> createBarData(time)
            "BaseLine" -> createLineData(time)
            "Volume" -> createVolumeData(time)
            "Area" -> createAreaData(time)
        }
    }

    private fun createLineData(time: String) {
        viewModelScope.launch(IO) {
            try {
                if (time == "all") {
                    val price = _priceDaily
                    for (item in price[0].historical.reversed()) {
                        data.add(LineData(Time.StringTime(item.date!!), item.close!!))
                    }
                } else {
                    val price = _priceChartMH
                    for (item in price.reversed()) {
                        val strDate = LocalDateTime.parse(item.date!!,
                            ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val timestamp = LocalDateTime.of(strDate.year, strDate.monthValue,
                        strDate.dayOfMonth, strDate.hour, strDate.minute, strDate.second).
                        toEpochSecond(ZoneOffset.UTC)
                        data.add(LineData(Time.Utc(timestamp), item.close!!))
                    }
                }
                withContext(Main) {
                    _dataStatus.value = StockStatus.DONE
                }
            } catch (e: Exception) {
                withContext(Main) {
                    _dataStatus.value = StockStatus.ERROR
                }
                Log.e("Data Error", "Failed to create line data!, ${e.message}")
            }
        }
    }

    private fun createVolumeData(time: String) {
        viewModelScope.launch(IO) {
            try {
                if (time == "all") {
                    val volume = _priceDaily
                    var prefVolume = 0f
                    for (item in volume[0].historical.reversed()) {
                        if (item.volume!! >= prefVolume) {
                            data.add(
                                HistogramData(
                                    Time.StringTime(item.date!!), item.volume!!,
                                    Color.parseColor("#26a69a").toIntColor()
                                )
                            )
                            prefVolume = item.volume!!
                        } else {
                            data.add(
                                HistogramData(
                                    Time.StringTime(item.date!!), item.volume!!,
                                    Color.parseColor("#ef5350").toIntColor()
                                )
                            )
                            prefVolume = item.volume!!
                        }
                    }
                } else {
                    val volume = _priceChartMH
                    var prefVolume = 0f
                    for (item in volume.reversed()) {
                        val strDate = LocalDateTime.parse(item.date!!,
                            ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val timestamp = LocalDateTime.of(strDate.year, strDate.monthValue,
                            strDate.dayOfMonth, strDate.hour, strDate.minute, strDate.second).
                        toEpochSecond(ZoneOffset.UTC)
                        if (item.volume!! >= prefVolume) {
                            data.add(
                                HistogramData(
                                    Time.Utc(timestamp), item.volume!!,
                                    Color.parseColor("#26a69a").toIntColor()
                                )
                            )
                            prefVolume = item.volume!!
                        } else {
                            data.add(
                                HistogramData(
                                    Time.Utc(timestamp), item.volume!!,
                                    Color.parseColor("#ef5350").toIntColor()
                                )
                            )
                            prefVolume = item.volume!!
                        }
                    }
                }
                withContext(Main) {
                    _dataStatus.value = StockStatus.DONE
                }
            } catch (e: Exception) {
                withContext(Main) {
                    _dataStatus.value = StockStatus.ERROR
                }
                Log.e("Data Error", "Failed to create volume data!, ${e.message}")
            }
        }
    }

    private fun createCandleData(time: String) {
        viewModelScope.launch(IO) {
            try {
                if (time == "all") {
                    val price = _priceDaily
                    for (item in price[0].historical.reversed()) {
                        data.add(
                            CandlestickData(
                                Time.StringTime(item.date!!), item.open!!,
                                item.high!!, item.low!!, item.close!!
                            )
                        )
                    }
                } else {
                    val price = _priceChartMH
                    for (item in price.reversed()) {
                        val strDate = LocalDateTime.parse(item.date!!,
                            ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val timestamp = LocalDateTime.of(strDate.year, strDate.monthValue,
                            strDate.dayOfMonth, strDate.hour, strDate.minute, strDate.second).
                        toEpochSecond(ZoneOffset.UTC)
                        data.add(CandlestickData(Time.Utc(timestamp), item.open!!, item.high!!,
                        item.low!!, item.close!!))
                    }
                }
                withContext(Main) {
                    _dataStatus.value = StockStatus.DONE
                }
            } catch (e: Exception) {
                withContext(Main) {
                    _dataStatus.value = StockStatus.ERROR
                }
                Log.e("Data Error", "Failed to create candle stick data!")
            }
        }
    }

    private fun createBarData(time: String) {
        viewModelScope.launch(IO) {
            try {
                if (time == "all") {
                    val price = _priceDaily
                    for (item in price[0].historical.reversed()) {
                        data.add(
                            BarData(Time.StringTime(item.date!!), item.open!!,
                                item.high!!, item.low!!, item.close!!)
                        )
                    }
                } else {
                    val price = _priceChartMH
                    for (item in price.reversed()) {
                        val strDate = LocalDateTime.parse(item.date!!,
                            ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val timestamp = LocalDateTime.of(strDate.year, strDate.monthValue,
                            strDate.dayOfMonth, strDate.hour, strDate.minute, strDate.second).
                        toEpochSecond(ZoneOffset.UTC)
                        data.add(BarData(Time.Utc(timestamp), item.open!!, item.high!!,
                            item.low!!, item.close!!))
                    }
                }
                withContext(Main) {
                    _dataStatus.value = StockStatus.DONE
                }
            } catch (e: Exception) {
                withContext(Main) {
                    _dataStatus.value = StockStatus.ERROR
                }
                Log.e("Data Error", "Failed to create candle stick data!")
            }
        }
    }

    private fun createAreaData(time: String) {
        viewModelScope.launch(IO) {
            try {
                if (time == "all") {
                    val price = _priceDaily
                    for (item in price[0].historical.reversed()) {
                        data.add(LineData(Time.StringTime(item.date!!), item.close!!))
                    }
                } else {
                    val price = _priceChartMH
                    for (item in price.reversed()) {
                        val strDate = LocalDateTime.parse(item.date!!,
                            ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val timestamp = LocalDateTime.of(strDate.year, strDate.monthValue,
                            strDate.dayOfMonth, strDate.hour, strDate.minute, strDate.second).
                        toEpochSecond(ZoneOffset.UTC)
                        data.add(LineData(Time.Utc(timestamp), item.close!!))
                    }
                }
                withContext(Main) {
                    _dataStatus.value = StockStatus.DONE
                }
            } catch (e: Exception) {
                withContext(Main) {
                    _dataStatus.value = StockStatus.ERROR
                }
                Log.e("Data Error", "Failed to create area data!, ${e.message}")
            }
        }
    }
}