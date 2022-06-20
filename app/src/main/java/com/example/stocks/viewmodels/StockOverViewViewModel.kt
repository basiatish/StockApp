package com.example.stocks.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocks.*
import com.example.stocks.models.remote.*
import com.example.stocks.utils.network.StockApiStatus
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StockOverViewViewModel: ViewModel() {

    private val apiKey = BuildConfig.API_KEY

    private val _status = MutableLiveData<StockApiStatus>()

    val status: LiveData<StockApiStatus> = _status

    private val _priceChart = MutableLiveData<MutableList<StockChart>>()

    val priceChart: LiveData<MutableList<StockChart>> = _priceChart

    private val _priceChartDaily = MutableLiveData<MutableList<StockDailyPriceHeader>>()

    val priceChartDaily: LiveData<MutableList<StockDailyPriceHeader>> = _priceChartDaily

    private var _priceChartYearly = MutableLiveData<MutableList<StockYearlyPriceHeader>>()

    val priceChartYearly: LiveData<MutableList<StockYearlyPriceHeader>> = _priceChartYearly

    private var _companyProfile = MutableLiveData<MutableList<CompanyProfile>>()

    val companyProfile: LiveData<MutableList<CompanyProfile>> = _companyProfile

    private var _companyDividends = MutableLiveData<MutableList<StockDividendsHeader>>()

    val companyDividends: LiveData<MutableList<StockDividendsHeader>> = _companyDividends

    fun getChartData(time: String, compName: String) {
        viewModelScope.launch {
            _status.value = StockApiStatus.LOADING
            try {
                if (time == "1day" || time == "1week") {
                    _priceChartDaily.value = mutableListOf(
                        StockApi.retrofitService
                            .getFullHistoryDailyPrice(compName, apiKey))
                } else if (time == "all") {
                    _priceChartYearly.value = mutableListOf(
                        StockApi.retrofitService
                            .getFullHistoryPrice(compName, "line", apiKey))
                } else {
                    _priceChart.value = StockApi.retrofitService
                        .getChart(time, compName, apiKey)
                }
                _status.value = StockApiStatus.DONE
            } catch (e: Exception) {
                _status.value = StockApiStatus.ERROR
                _priceChart.value = mutableListOf()
                println(e.message)
            }
        }
    }

    fun getCompanyProfile(compName: String) {
        viewModelScope.launch {
            try {
                _companyProfile.value = StockApi.retrofitService.
                getCompanyProfile(compName, apiKey)
            } catch (e: Exception) {
                _companyProfile.value = mutableListOf()
                println(e.message)
            }
        }
    }

    fun getCompanyDividends(compName: String) {
        viewModelScope.launch {
            try {
                _companyDividends.value = mutableListOf(
                    StockApi.retrofitService.getCompanyDividends(compName, apiKey))
            } catch (e: Exception) {
                _companyDividends.value = mutableListOf()
                println(e.message)
            }
        }
    }

    fun clearData() {
        _priceChart.value?.clear()
        _priceChartDaily.value?.clear()
        _priceChartYearly.value?.clear()
        _companyProfile.value?.clear()
    }

    fun getLineChart(range: String): MutableList<Entry> {
        val entryData = mutableListOf<Entry>()
        if (range == "1week") {
            val priceDaily = priceChartDaily.value
            var j = 0
            if (range == "1week") {
                for ((i, item) in priceDaily?.get(0)?.historical!!.reversed().withIndex()) {
                    if (i % 5 == 0) {
                        entryData.add(Entry(i.toFloat(), item.close!!.toFloat()))
                    }
                }
            }
        } else if (range == "all") {
            val priceYearly = priceChartYearly.value
            for ((i, item) in priceYearly?.get(0)?.historical!!.reversed().withIndex()) {
                entryData.add(Entry(i.toFloat(), item.close!!.toFloat()))
            }
        } else {
            val price = priceChart.value!!
            for ((i, item) in price.reversed().withIndex()) {
                entryData.add(Entry(i.toFloat(), item.close!!.toFloat()))
            }
        }
        return entryData
    }

    fun getLineChartDate(time: String): MutableList<String> {
        val date = mutableListOf<String>()
        if (time == "1day" || time == "1week") {
            val priceDaily = priceChartDaily.value!!
            for ((i, item) in priceDaily[0].historical.reversed().withIndex()) {
                if (time == "1week" && (i % 5) == 0) {
                    date.add(item.date.toString())
                } else if(time == "1day") {
                    date.add(item.date.toString())
                }
            }
        } else if (time == "all") {
            val priceYearly = priceChartYearly.value!!
            for (item in priceYearly[0].historical.reversed()) {
                date.add(item.date.toString())
            }
        } else {
            val price = priceChart.value!!
            for (item in price.reversed()) {
                date.add(item.date.toString())
            }
        }

        return date
    }

    fun getBarChart(): MutableList<BarEntry> {
        val entryData = mutableListOf<BarEntry>()
        val data = companyDividends.value?.get(0)?.historical
        for ((i, item) in data?.reversed()?.withIndex()!!) {
            entryData.add(BarEntry(i.toFloat(), item.dividend!!.toFloat()))
        }
        return entryData
    }

    @SuppressLint("SimpleDateFormat")
    fun getClosePrice(price: MutableList<StockChart>): Float {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        var closePrice = 0f
        for (i in 0 until price.size) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val date = "${sdf.format(calendar.time)} 16:00:00"
            price.forEach { item ->
                if (item.date.equals(date, true)) {
                    closePrice = item.close!!
                    return closePrice
                }
            }
        }
        return closePrice
    }
}