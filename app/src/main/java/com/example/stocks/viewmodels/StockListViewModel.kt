package com.example.stocks.viewmodels

import androidx.lifecycle.*
import com.example.stocks.database.stocksdatabase.Stock
import com.example.stocks.database.stocksdatabase.StockDao
import kotlinx.coroutines.launch

class StockListViewModel(private val stockDao: StockDao) : ViewModel() {

    private val _stockList = MutableLiveData<List<Stock>>()
    val stockList: LiveData<List<Stock>> get() = _stockList

    fun getStocks() {
        viewModelScope.launch {
            _stockList.value = stockDao.getAlerts()
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