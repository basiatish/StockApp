package com.example.stocks.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.stocks.database.stocksdatabase.Stock
import com.example.stocks.database.stocksdatabase.StockDao
import com.example.stocks.models.remote.CompanyQuote
import com.example.stocks.network.StockApi
import com.example.stocks.utils.formatters.Formatter
import com.example.stocks.utils.network.StockStatus
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockListViewModel(private val stockDao: StockDao) : ViewModel() {

    private var _stockList: List<Stock> = listOf()
    val stockList: List<Stock> get() = _stockList

    private val _stockListStatus = MutableLiveData<StockStatus>()
    val stockListStatus: LiveData<StockStatus> get() = _stockListStatus

    private var _removeStockList: MutableList<Stock> = mutableListOf()
    val removeStockList: List<Stock> get() = _removeStockList

    private val _isRemoveListEmpty = MutableLiveData<Boolean>()
    val isRemoveListEmpty: LiveData<Boolean> = _isRemoveListEmpty

    private var _companiesQuote: MutableList<CompanyQuote> = mutableListOf()

    private val _quoteStatus = MutableLiveData<StockStatus>()
    val quoteStatus: LiveData<StockStatus> = _quoteStatus

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

    fun updateStock() {
        viewModelScope.launch(IO) {
            try {
                var count = 0
                _companiesQuote.forEach { quote ->
                    val stock = createStockObject(quote, count)
                    stockDao.update(stock)
                    count++
                }
                getStocks()
            } catch (e: Exception) {
                Log.e("Update DataBase", "${e.message}")
            }
        }
    }

    private fun createStockObject(quote: CompanyQuote, index: Int): Stock {
        return Stock().apply {
            setId(_stockList[index].id)
            setShortName(quote.symbol)
            setName(quote.name)
            setPrice(Formatter().cutZeros(quote.price ?: 0.0).toDouble())
            setPriceChange(Formatter().cutZeros(quote.change ?: 0.0).toDouble())
            setPriceChangePercent(
                Formatter().cutZeros(quote.changesPercentage ?: 0.0).toDouble()
            )
            setUrl(_stockList[index].url)
        }
    }

    fun updateStockList(stock: Stock, remove: Boolean) {
        if (remove) {
            _removeStockList.add(stock)
            if (_removeStockList.isNotEmpty()) {
                _isRemoveListEmpty.value = false
            }
            Log.i("Remove", "Added to remove list ${stock.price}")
        }
        else {
            _removeStockList.remove(stock)
            if (_removeStockList.isEmpty()) {
                _isRemoveListEmpty.value = true
            }
            Log.i("Remove", "Removed from remove list ${stock.price}")
        }
    }

    fun deleteStock() {
        Log.i("Remove", "Delete status: Size ${_removeStockList.size}")
        viewModelScope.launch(IO) {
            _removeStockList.forEach { stock ->
                stockDao.delete(stock)
            }
            getStocks()
            withContext(Main) {
                _isRemoveListEmpty.value = true
            }
        }
    }

    fun getStockPrice(apiKey: String) {
        Log.e("Update123", "API Request")
        viewModelScope.launch {
            _quoteStatus.value = StockStatus.LOADING
            try {
                withContext(IO) {
                    _companiesQuote = StockApi.retrofitService.getCompanyQuote(createCompaniesList(), apiKey)
                }
                _quoteStatus.value = StockStatus.DONE
            } catch (e: Exception) {
                _quoteStatus.value = StockStatus.ERROR
            }
        }
    }

    private fun createCompaniesList(): String {
        var str = ""
        for (i in _stockList.indices) {
            if (i == _stockList.size - 1) str += _stockList[i].shortName
            else str = str + _stockList[i].shortName + ','
        }
        return str
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