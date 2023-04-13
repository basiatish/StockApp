package com.example.stocks

import android.app.Application
import androidx.work.*
import com.example.stocks.database.alertdatabase.AlertRoomDataBase
import com.example.stocks.database.stocksdatabase.StockRoomDataBase
import com.example.stocks.workers.PriceAlarmWork
import java.util.concurrent.TimeUnit

class App : Application() {
    val alertDataBase: AlertRoomDataBase by lazy {
        AlertRoomDataBase.getDataBase(this)
    }

    val stockDataBase: StockRoomDataBase by lazy {
        StockRoomDataBase.getDataBase(this)
    }

    fun scheduleWork(tag: String, compName: String, price: Double, time: Long, above: Boolean) {
        val workManager = WorkManager.getInstance(this)
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val data = Data.Builder()
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