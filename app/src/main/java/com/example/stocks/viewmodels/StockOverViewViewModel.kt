package com.example.stocks.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocks.*
import com.example.stocks.models.remote.*
import com.example.stocks.utils.network.StockApiStatus
import kotlinx.coroutines.launch

class StockOverViewViewModel: ViewModel() {

    private val apiKey = BuildConfig.API_KEY

    private val _status = MutableLiveData<StockApiStatus>()

    val status: MutableLiveData<StockApiStatus> get() = _status

    private val _profileStatus = MutableLiveData<StockApiStatus>()

    val profileStatus: LiveData<StockApiStatus> = _profileStatus

    private val _quoteStatus = MutableLiveData<StockApiStatus>()

    val quoteStatus: LiveData<StockApiStatus> get() = _quoteStatus

    private val _dividendsStatus = MutableLiveData<StockApiStatus>()

    val dividendsStatus: LiveData<StockApiStatus> get() = _dividendsStatus

    private val _divListStatus = MutableLiveData<Boolean>(false)

    val divListStatus: LiveData<Boolean> get() = _divListStatus

    private var _companyProfile = MutableLiveData<MutableList<CompanyProfile>>()

    val companyProfile: LiveData<MutableList<CompanyProfile>> = _companyProfile

    private var _companyQuote = MutableLiveData<MutableList<CompanyQuote>>()

    val companyQuote: LiveData<MutableList<CompanyQuote>> = _companyQuote

    private var _companyDividends = MutableLiveData<MutableList<StockDividendsHeader>>()

    val companyDividends: LiveData<MutableList<StockDividendsHeader>> = _companyDividends

    private var _companyDividendsList = MutableLiveData<MutableList<StockDividends>>()

    val companyDividendsList: LiveData<MutableList<StockDividends>> = _companyDividendsList

    fun getCompanyProfile(compName: String) {
        viewModelScope.launch {
            _profileStatus.value = StockApiStatus.LOADING
            try {
                _companyProfile.value = StockApi.retrofitService.
                getCompanyProfile(compName, apiKey)
                _profileStatus.value = StockApiStatus.DONE
            } catch (e: Exception) {
                _companyProfile.value = mutableListOf()
                _profileStatus.value = StockApiStatus.ERROR
                println(e.message)
            }
        }
    }

    fun getCompanyQuote(compName: String) {
        viewModelScope.launch {
            _quoteStatus.value = StockApiStatus.LOADING
            try {
                _companyQuote.value = StockApi.retrofitService.getCompanyQuote(compName, apiKey)
                _quoteStatus.value = StockApiStatus.DONE
            } catch (e: Exception) {
                _companyQuote.value = mutableListOf()
                _quoteStatus.value = StockApiStatus.ERROR
                println(e.message)
            }
        }
    }

    fun getCompanyDividends(compName: String) {
        viewModelScope.launch {
            _dividendsStatus.value = StockApiStatus.LOADING
            try {
                _companyDividends.value = mutableListOf(
                    StockApi.retrofitService.getCompanyDividends(compName, apiKey))
                _dividendsStatus.value = StockApiStatus.DONE
            } catch (e: Exception) {
                _companyDividends.value = mutableListOf()
                _dividendsStatus.value = StockApiStatus.ERROR
                println(e.message)
            }
        }
    }

    fun getDividendsList() {
        viewModelScope.launch {
            var status = _divListStatus.value!!
            val size = _companyDividends.value!![0].historical.size
            if (!status) {
                if (size < 5) {
                    _companyDividendsList.value = _companyDividends.value!![0].
                    historical.toMutableList().subList(0,size)
                } else {
                    _companyDividendsList.value = _companyDividends.value!![0].
                    historical.toMutableList().subList(0, 5)
                }
                status = true
            } else {
                _companyDividendsList.value = _companyDividends.value!![0].
                historical.toMutableList()
                status = false
            }
            _divListStatus.value = status
        }
    }

//    fun getLineChart(range: String): MutableList<Entry> {
//        val entryData = mutableListOf<Entry>()
//        if (range == "1week") {
//            val priceDaily = priceChartDaily.value
//            var j = 0
//            if (range == "1week") {
//                for ((i, item) in priceDaily?.get(0)?.historical!!.reversed().withIndex()) {
//                    if (i % 5 == 0) {
//                        entryData.add(Entry(i.toFloat(), item.close!!.toFloat()))
//                    }
//                }
//            }
//        } else if (range == "all") {
//            val priceYearly = priceChartYearly.value
//            for ((i, item) in priceYearly?.get(0)?.historical!!.reversed().withIndex()) {
//                entryData.add(Entry(i.toFloat(), item.close!!.toFloat()))
//            }
//        } else {
//            val price = priceChart.value!!
//            for ((i, item) in price.reversed().withIndex()) {
//                entryData.add(Entry(i.toFloat(), item.close!!.toFloat()))
//            }
//        }
//        return entryData
//    }
//
//    fun getLineChartDate(time: String): MutableList<String> {
//        val date = mutableListOf<String>()
//        if (time == "1day" || time == "1week") {
//            val priceDaily = priceChartDaily.value!!
//            for ((i, item) in priceDaily[0].historical.reversed().withIndex()) {
//                if (time == "1week" && (i % 5) == 0) {
//                    date.add(item.date.toString())
//                } else if(time == "1day") {
//                    date.add(item.date.toString())
//                }
//            }
//        } else if (time == "all") {
//            val priceYearly = priceChartYearly.value!!
//            for (item in priceYearly[0].historical.reversed()) {
//                date.add(item.date.toString())
//            }
//        } else {
//            val price = priceChart.value!!
//            for (item in price.reversed()) {
//                date.add(item.date.toString())
//            }
//        }
//
//        return date
//    }
//
//    fun getBarChart(): MutableList<BarEntry> {
//        val entryData = mutableListOf<BarEntry>()
//        val data = companyDividends.value?.get(0)?.historical
//        for ((i, item) in data?.reversed()?.withIndex()!!) {
//            entryData.add(BarEntry(i.toFloat(), item.dividend!!.toFloat()))
//        }
//        return entryData
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    fun getClosePrice(price: MutableList<StockChart>): Float {
//        val calendar = Calendar.getInstance()
//        val sdf = SimpleDateFormat("yyyy-MM-dd")
//        var closePrice = 0f
//        for (i in 0 until price.size) {
//            calendar.add(Calendar.DAY_OF_MONTH, -1)
//            val date = "${sdf.format(calendar.time)} 16:00:00"
//            price.forEach { item ->
//                if (item.date.equals(date, true)) {
//                    closePrice = item.close!!
//                    return closePrice
//                }
//            }
//        }
//        return closePrice
//    }
//
//    private val _lineSeriesData = MutableLiveData<MutableList<SeriesData>>()
//
//    val lineSeriesData: LiveData<MutableList<SeriesData>> = _lineSeriesData
//
//
//    fun setupChartData(): MutableList<SeriesData> {
//        val price = _priceChartDaily.value!!
////        for (item in price[0].historical.reversed()) {
////            lineSeriesData.value?.add(LineData(Time.StringTime(item.date!!), item.close!!))
////        }

//        val data: MutableList<SeriesData> = mutableListOf()
//
//        for (item in price[0].historical.reversed()) {
//            data.add(LineData(Time.StringTime(item.date!!), item.close!!))
//        }
//        return data
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