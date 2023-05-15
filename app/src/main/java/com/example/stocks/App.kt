package com.example.stocks

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.example.stocks.database.alertdatabase.AlertRoomDataBase
import com.example.stocks.database.stocksdatabase.StockRoomDataBase
import com.example.stocks.workers.PriceAlarmWork
import java.util.concurrent.TimeUnit

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val isThemeLight = (applicationContext as App).getUiMode()
        if (isThemeLight) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    val alertDataBase: AlertRoomDataBase by lazy {
        AlertRoomDataBase.getDataBase(this)
    }

    val stockDataBase: StockRoomDataBase by lazy {
        StockRoomDataBase.getDataBase(this)
    }

    private val preferences: SharedPreferences by lazy {
        getSharedPreferences("App Settings", Context.MODE_PRIVATE)
    }

    fun saveApiKey(apiKey: String) {
        preferences.edit().putString("apiKey", apiKey).apply()
    }

    fun getApiKey(): String? {
        return preferences.getString("apiKey", null)
    }

    fun setUI(isLightMode: Boolean) {
        if (isLightMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        saveUiMode(isLightMode)
    }

    private fun saveUiMode(value: Boolean) {
        preferences.edit().putBoolean("uiMode", value).apply()
    }

    fun getUiMode(): Boolean {
        return preferences.getBoolean("uiMode", true)
    }

    fun scheduleWork(id: Int, tag: String, compName: String, price: Double, time: Long, above: Boolean) {
        val workManager = WorkManager.getInstance(this)
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val data = Data.Builder()
            .putInt("id", id)
            .putString("name", compName)
            .putDouble("price", price)
            .putLong("time", time)
            .putBoolean("above", above)
            .build()

        val myWorkRequest = PeriodicWorkRequestBuilder<PriceAlarmWork>(repeatInterval = 1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInputData(data)
            .addTag(tag)
            .build()

        workManager.enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.REPLACE, myWorkRequest)
    }

    fun destroyWork(tag: String) {
        val workManager = WorkManager.getInstance(this)
        workManager.cancelAllWorkByTag(tag)
    }

}