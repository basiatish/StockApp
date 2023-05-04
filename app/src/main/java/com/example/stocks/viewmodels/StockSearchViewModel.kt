package com.example.stocks.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.stocks.BuildConfig
import com.example.stocks.StockApi
import com.example.stocks.database.stocksdatabase.StockDao
import com.example.stocks.models.remote.StockSearch
import com.example.stocks.utils.network.StockStatus
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockSearchViewModel(private val stockDao: StockDao): ViewModel() {

    private val _requestStatus = MutableLiveData<StockStatus>()
    val requestStatus: LiveData<StockStatus> get() =  _requestStatus

    private val _listStatus = MutableLiveData<StockStatus>()
    val listStatus: LiveData<StockStatus> get() =  _listStatus

    private var _companies: List<StockSearch> = listOf()
    val companies: List<StockSearch> get() = _companies

    private var _companiesList: MutableList<StockSearch> = mutableListOf()
    val companiesList: List<StockSearch> get() = _companiesList

    private val apiKey = BuildConfig.API_KEY

    fun findCompany(name: String) {
        viewModelScope.launch {
            _companies = listOf()
            _requestStatus.value = StockStatus.LOADING
            try {
                withContext(IO) {
                    _companies = StockApi.retrofitService
                        .getCompanies(name, "30", "NYSE,NASDAQ", apiKey)
                }
                _requestStatus.value = StockStatus.DONE
            } catch (e: Exception) {
                Log.e("Search error", "${e.message}")
                _requestStatus.value = StockStatus.ERROR
            }
        }
    }

    fun createCompaniesList() {
        viewModelScope.launch {
            _companiesList = mutableListOf()
            _listStatus.value = StockStatus.LOADING
            try {
                withContext(IO) {
                    val favoriteStocks = stockDao.getStocks()
                    if (favoriteStocks.isNotEmpty()) {
                        val tickers: MutableList<String> = mutableListOf()
                        favoriteStocks.forEach {
                            tickers.add(it.shortName)
                        }
                        for (item in _companies) {
                            item.favorite = false
                            if (favoriteStocks.any { it.shortName == item.symbol }) {
                                item.favorite = true
                                _companiesList.add(item)
                            }
                        }
                        for (item in _companies) {
                            if (!item.favorite) {
                                _companiesList.add(item)
                            }
                        }
                    } else {
                        for (item in _companies) {
                            item.favorite = false
                            _companiesList.add(item)
                        }
                    }
                }
                _listStatus.value = StockStatus.DONE
            } catch (e: Exception) {
                Log.e("Search error", "${e.message}")
                _listStatus.value = StockStatus.ERROR
            }
        }
    }

}

class StockSearchViewModelFactory(private val stockDao: StockDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockSearchViewModel::class.java)) {
            return StockSearchViewModel(stockDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}