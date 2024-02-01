package com.basiatish.stocks.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.basiatish.stocks.database.stocksdatabase.Stock
import com.basiatish.stocks.database.stocksdatabase.StockDao
import com.basiatish.stocks.models.remote.CompanyProfile
import com.basiatish.stocks.models.remote.CompanyQuote
import com.basiatish.stocks.models.remote.StockDividends
import com.basiatish.stocks.models.remote.StockDividendsHeader
import com.basiatish.stocks.network.StockApi
import com.basiatish.stocks.utils.formatters.Formatter
import com.basiatish.stocks.utils.network.StockStatus
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockOverViewViewModel(private val stockDao: StockDao): ViewModel() {

    private val _profileStatus = MutableLiveData<StockStatus>()
    val profileStatus: LiveData<StockStatus> = _profileStatus

    private val _quoteStatus = MutableLiveData<StockStatus>()
    val quoteStatus: LiveData<StockStatus> = _quoteStatus

    private val _dividendsStatus = MutableLiveData<StockStatus>()
    val dividendsStatus: LiveData<StockStatus> = _dividendsStatus

    private val _divListStatus = MutableLiveData<StockStatus>()
    val divListStatus: LiveData<StockStatus> = _divListStatus

    private lateinit var _companyProfile: MutableList<CompanyProfile>
    val companyProfile: List<CompanyProfile> get() = _companyProfile

    private var _companyQuote: MutableList<CompanyQuote> = mutableListOf()
    val companyQuote: List<CompanyQuote> get() = _companyQuote

    private var _companyDividends: MutableList<StockDividendsHeader> = mutableListOf()
    val companyDividends: List<StockDividendsHeader> get() = _companyDividends

    private var _companyDividendsList: MutableList<StockDividends> = mutableListOf()
    val companyDividendsList: List<StockDividends> get() = _companyDividendsList

    private var dividendsListState = false

    private var _isFavourite = MutableLiveData<String?>()
    val isFavourite: LiveData<String?> get() = _isFavourite

    fun saveStock(shortName: String, name: String) {
        viewModelScope.launch(IO) {
            try {
                val stock = createStockObject(shortName, name)
                stockDao.insert(stock)
            } catch (e: Exception) {
                Log.e("Error", "${e.message}")
            }
        }
    }

    fun updateStock(shortName: String, name: String) {
        viewModelScope.launch(IO) {
            try {
                val stock = createStockObject(shortName, name)
                stockDao.update(stock)
            } catch (e: Exception) {
                Log.e("Error", "${e.message}")
            }
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

    private fun createStockObject(shortName: String, name: String): Stock {
        return Stock().apply {
            setShortName(shortName)
            setName(name)
            if (_companyQuote.isNotEmpty()) {
                setPrice(cutValueZeros(_companyQuote[0].price ?: 0.0).toDouble())
                setPriceChange(cutValueZeros(_companyQuote[0].change ?: 0.0).toDouble())
                setPriceChangePercent(
                    cutValueZeros(
                        _companyQuote[0].changesPercentage ?: 0.0
                    ).toDouble()
                )
                setUrl(_companyProfile[0].image)
            } else {
                setPrice(0.0)
                setPriceChange(0.0)
                setPriceChangePercent(0.0)
                setUrl("")
            }
        }
    }

    fun getCompanyProfile(compName: String, apiKey: String) {
        _companyProfile = mutableListOf()
        viewModelScope.launch {
            _profileStatus.value = StockStatus.LOADING
            try {
            withContext(IO) {
                _companyProfile = StockApi.retrofitService.getCompanyProfile(compName, apiKey)
            }
                _profileStatus.value = StockStatus.DONE
                Log.i("Status", "Profile status ${_profileStatus.value}")
            } catch (e: Exception) {
                _profileStatus.value = StockStatus.ERROR
                println("Loading profile error ${e.message}")
            }
        }
    }

    fun getCompanyQuote(compName: String, apiKey: String) {
        _companyQuote = mutableListOf()
        viewModelScope.launch() {
            _quoteStatus.value = StockStatus.LOADING
            try {
                withContext(IO) {
                    _companyQuote = StockApi.retrofitService.getCompanyQuote(compName, apiKey)
                }
                _quoteStatus.value = StockStatus.DONE
                Log.e("Status", "Quote status ${_quoteStatus.value}")
            } catch (e: Exception) {
                _quoteStatus.value = StockStatus.ERROR
                Log.e("Status", "Quote status ${_quoteStatus.value}, ${e.message}")
                println(e.message)
            }
        }
    }

    fun getCompanyDividends(compName: String, apiKey: String) {
        _companyDividends = mutableListOf()
        viewModelScope.launch {
            _dividendsStatus.value = StockStatus.LOADING
            try {
                withContext(IO) {
                    _companyDividends = mutableListOf(
                        StockApi.retrofitService.getCompanyDividends(compName, apiKey))
                }
                _dividendsStatus.value = StockStatus.DONE
            } catch (e: Exception) {
                _dividendsStatus.value = StockStatus.ERROR
                println(e.message)
            }
        }
    }

    fun getDividendsList() {
        _companyDividendsList = mutableListOf()
        viewModelScope.launch {
            try {
                withContext(Default) {
                    var status = dividendsListState
                    val size = _companyDividends[0].historical.size
                    if (!status) {
                        if (size < 5) {
                            _companyDividendsList =
                                _companyDividends[0].historical.toMutableList().subList(0, size)
                        } else {
                            _companyDividendsList =
                                _companyDividends[0].historical.toMutableList().subList(0, 5)
                        }
                        status = true
                    } else {
                        _companyDividendsList = _companyDividends[0].historical.toMutableList()
                        status = false
                    }
                    dividendsListState = status
                }
                if (_companyDividendsList.isEmpty()) {
                    _divListStatus.value = StockStatus.ERROR
                } else {
                    _divListStatus.value = StockStatus.DONE
                }
                Log.e("Status", "DivListStatus ${_divListStatus.value}")
            } catch (e: Exception) {
                _divListStatus.value = StockStatus.ERROR
                Log.e("Status", "DivListStatus ${_divListStatus.value}")
            }

        }
    }

    fun formatValue(value: Double): String {
        val formatter = Formatter()
        return formatter.formattedValue(value)
    }

    fun formatRange(range: String): Array<String> {
        val formatter = Formatter()
        return formatter.formattedRange(range)
    }

    fun cutValueZeros(value: Double): String {
        val formatter = Formatter()
        return formatter.cutZeros(value)
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