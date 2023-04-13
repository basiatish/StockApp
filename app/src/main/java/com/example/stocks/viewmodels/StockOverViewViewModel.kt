package com.example.stocks.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.stocks.*
import com.example.stocks.database.stocksdatabase.Stock
import com.example.stocks.database.stocksdatabase.StockDao
import com.example.stocks.models.remote.*
import com.example.stocks.utils.network.StockApiStatus
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockOverViewViewModel(private val stockDao: StockDao): ViewModel() {

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

    private var _isFavourite = MutableLiveData<String?>()
    val isFavourite: LiveData<String?> get() = _isFavourite

    fun saveStock(shortName: String, name: String, price: Double, priceChange: Double, priceChangePercent: Double) {
        val stock = createStockObject(shortName, name, price, priceChange, priceChangePercent)
        viewModelScope.launch(IO) {
            stockDao.insert(stock)
        }
    }

    fun updateStock(shortName: String, name: String, price: Double, priceChange: Double, priceChangePercent: Double) {
        val stock = createStockObject(shortName, name, price, priceChange, priceChangePercent)
        viewModelScope.launch(IO) {
            stockDao.update(stock)
        }
    }

    fun deleteStock(shortName: String) {
        viewModelScope.launch(IO) {
          stockDao.deleteByName(shortName)
        }
    }

    fun isStockFavourite(shortName: String) {
        viewModelScope.launch {
            _isFavourite.value = stockDao.isExist(shortName)
        }
    }

    private fun createStockObject(
        shortName: String,
        name: String,
        price: Double,
        priceChange: Double,
        priceChangePercent: Double
    ): Stock {
        return Stock().apply {
            setShortName(shortName)
            setName(name)
            setPrice(price)
            setPriceChange(priceChange)
            setPriceChangePercent(priceChangePercent)
        }
    }

    fun getComp(compName: String) {
        getCompanyProfile(compName)
    }

    fun complist() {
        _companyProfile.value = compProf
    }

    var compProf: MutableList<CompanyProfile> = mutableListOf()

    fun getCompanyProfile(compName: String) {
        viewModelScope.launch(IO) {
            try {
                compProf = StockApi.retrofitService.
                getCompanyProfile(compName, apiKey)
                withContext(Main) {
                    _profileStatus.value = StockApiStatus.DONE
                    Log.d("Status", "Profile status ${_profileStatus.value}")
                }
            } catch (e: Exception) {
                compProf = mutableListOf()
                println("Loading profile error ${e.message}")
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
}

class StockOverViewViewModelFactory(private val stockDao: StockDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockOverViewViewModel::class.java)) {
            return StockOverViewViewModel(stockDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}