package com.example.stocks.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.stocks.App
import com.example.stocks.database.alertdatabase.AlertDao
import com.example.stocks.database.stocksdatabase.StockDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val stockDao: StockDao,
    private val alertDao: AlertDao,
    app: Application
) : AndroidViewModel(app) {

    fun deleteStocks() {
        try {
            viewModelScope.launch(IO) {
                stockDao.clearDatabase()
            }
        } catch (e: Exception) {
            Log.e("Stock Delete", "${e.message}")
        }
    }

    fun deleteAlerts() {
        try {
            viewModelScope.launch(IO) {
                alertDao.clearDataBase()
            }
        } catch (e: Exception) {
            Log.e("Alert Delete", "${e.message}")
        }
    }

    fun clearImagesCache() {
        viewModelScope.launch(IO) {
            Glide.get(getApplication<App>().applicationContext).clearDiskCache()
        }
    }
}

class SettingsViewModelFactory(
    private val stockDao: StockDao,
    private val alertDao: AlertDao,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(stockDao, alertDao, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}