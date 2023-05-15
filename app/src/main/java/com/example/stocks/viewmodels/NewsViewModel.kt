package com.example.stocks.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocks.models.remote.news.NewsContent
import com.example.stocks.models.remote.news.NewsContentHeader
import com.example.stocks.network.StockApi
import com.example.stocks.utils.formatters.Formatter
import com.example.stocks.utils.network.StockStatus
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel: ViewModel() {

    private var _newsList: MutableList<NewsContentHeader> = mutableListOf()
    val newsList: List<NewsContentHeader> get() = _newsList

    private var _news = MutableLiveData<NewsContent>()
    val news: LiveData<NewsContent> get() = _news

    private var _newsStatus = MutableLiveData<StockStatus>()
    val newsStatus: LiveData<StockStatus> get() = _newsStatus

    fun getNews(page: Int, apiKey: String) {
        viewModelScope.launch {
            _newsStatus.value = StockStatus.LOADING
            try {
                withContext(IO) {
                    _newsList = mutableListOf(StockApi.retrofitService.getNews(page, 10, apiKey))
                }
                _newsStatus.value = StockStatus.DONE
            } catch (e: Exception) {
                _newsStatus.value = StockStatus.ERROR
                _newsList = mutableListOf()
                Log.e("News", "${e.message}")
            }
        }
    }

    fun getUniqueNews(index: Int) {
        _news.value = _newsList[0].content[index]
    }

    fun formatText(str: String): String {
        return Formatter().formatHtml(str)
    }

    fun formatTicker(str: String): String {
        return Formatter().parseTicker(str)
    }

    fun formatDate(str: String): String {
        return Formatter().parseDate(str)
    }
}