package com.example.stocks

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.stocks.workers.PriceAlarm
import java.util.concurrent.TimeUnit

class App : Application() {

//    override fun onCreate() {
//        super.onCreate()
//        val  myWorkRequest = PeriodicWorkRequestBuilder<PriceAlarm>(repeatInterval = 15, TimeUnit.MINUTES).
//        addTag("kek").build()
//        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("kek",
//            ExistingPeriodicWorkPolicy.REPLACE, myWorkRequest)
//    }

}