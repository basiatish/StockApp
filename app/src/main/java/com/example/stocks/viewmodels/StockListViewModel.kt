package com.example.stocks.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.stocks.database.stocksdatabase.Stock
import com.example.stocks.database.stocksdatabase.StockDao
import com.example.stocks.utils.network.StockStatus
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockListViewModel(private val stockDao: StockDao) : ViewModel() {

    private var _stockList: List<Stock> = listOf()
    val stockList: List<Stock> get() = _stockList

    private val _stockListStatus = MutableLiveData<StockStatus>()
    val stockListStatus: LiveData<StockStatus> get() = _stockListStatus

    fun getStocks() {
        _stockList = listOf()
        viewModelScope.launch {
            _stockListStatus.value = StockStatus.LOADING
            try {
                withContext(IO) {
                    _stockList = stockDao.getStocks()
                }
                _stockListStatus.value = StockStatus.DONE
            } catch (e: Exception) {
                _stockListStatus.value = StockStatus.ERROR
                Log.e("StockList", "Error ${e.message}")
            }
        }
    }
}

class StockListViewModelFactory(private val stockDao: StockDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockListViewModel::class.java)) {
            return StockListViewModel(stockDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}