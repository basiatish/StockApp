package com.example.stocks.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocks.models.remote.news.NewsContentHeader
import com.example.stocks.network.StockApi
import com.example.stocks.utils.network.StockStatus
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApiKeyViewModel : ViewModel() {

    private var _status = MutableLiveData<StockStatus>()
    val status: LiveData<StockStatus> get() = _status

    private var _newsList: MutableList<NewsContentHeader> = mutableListOf()

    fun testApiKey(apiKey: String) {
        viewModelScope.launch {
            _status.value = StockStatus.LOADING
            try {
                withContext(IO) {
                    _newsList = mutableListOf(StockApi.retrofitService.getNews(0, 1, apiKey))
                }
                if (_newsList[0].content.isEmpty()) _status.value = StockStatus.ERROR
                else _status.value = StockStatus.DONE
            } catch (e: Exception) {
                _status.value = StockStatus.ERROR
            }
        }
    }

}