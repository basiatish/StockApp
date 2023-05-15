package com.example.stocks.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.stocks.App
import com.example.stocks.R
import com.example.stocks.database.alertdatabase.Alert
import com.example.stocks.models.remote.StockChart
import com.example.stocks.network.StockApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter.ofPattern

private const val TAG = "PriceAlarm"

class PriceAlarmWork(appContext: Context, params: WorkerParameters) : CoroutineWorker(
    appContext,
    params
) {

    private val notificationChannelName: CharSequence =
        "StockApp Notification Manager"
    private val notificationChannelDescription =
        "Shows notifications whenever target price reached"
    private lateinit var builder: Notification

    private val apiKey = (appContext.applicationContext as App).getApiKey()
    private var priceChartMH: MutableList<StockChart> = mutableListOf()
    private var id : Int = 0
    private var targetPrice: Double? = null
    private var timeCreated: Long? = null
    private var compName: String? = null
    private var above: Boolean = false

    private var tag = params.tags.elementAt(0)
    private var message = ""

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result = withContext(IO) {

        return@withContext try {
            id = inputData.getInt("id", 1)
            compName = inputData.getString("name")
            targetPrice = inputData.getDouble("price", 150.0)
            timeCreated = inputData.getLong("time", System.currentTimeMillis())
            above = inputData.getBoolean("above", false)

            priceChartMH = StockApi.retrofitService
                .getChart("1hour", compName!!, apiKey ?: "")

            val lastPrice = priceChartMH.reversed().last().close

            for (item in priceChartMH.reversed()) {
                val strDate = LocalDateTime.parse(
                    item.date!!,
                    ofPattern("yyyy-MM-dd HH:mm:ss")
                )
                val timestamp = LocalDateTime.of(
                    strDate.year, strDate.monthValue,
                    strDate.dayOfMonth, strDate.hour, strDate.minute, strDate.second
                ).toEpochSecond(ZoneOffset.UTC)
                if (timestamp > timeCreated!!) {
                    if (above) {
                        if (item.close!! > targetPrice!!) {
                            message = "Price is above $targetPrice! " +
                                    "Time: ${item.date}. " +
                                    "Current price is $lastPrice"
                            with(NotificationManagerCompat.from(applicationContext)) {
                                notify(id, createNotification())
                            }
                            WorkManager.getInstance(applicationContext).cancelAllWorkByTag(tag)
                            deactivateAlert()
                            break
                        }
                    } else {
                        if (item.close!! < targetPrice!!) {
                            message = "Price is below $targetPrice! " +
                                    "Time: ${item.date}. " +
                                    "Current price is $lastPrice"
                            with(NotificationManagerCompat.from(applicationContext)) {
                                notify(id, createNotification())
                            }
                            WorkManager.getInstance(applicationContext).cancelAllWorkByTag(tag)
                            deactivateAlert()
                            break
                        }
                    }
                }
            }
            Log.e(TAG, "Success")
            Result.success()
        } catch (th: Throwable) {
            Log.e(TAG, "Error $th")
            Result.failure()
        }
    }

    private suspend fun deactivateAlert() {
        (applicationContext as App).alertDataBase.alertDao().update(createAlertObject())
    }

    private fun createAlertObject() : Alert {
        return Alert(id = id, compName = compName!!, price = targetPrice!!, time = timeCreated!!,
            above = above, status = false)
    }

    private fun createNotification(): Notification {
        val id = "1225"
        val title = "Price Alert"

        val name = notificationChannelName
        val description = notificationChannelDescription
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
        channel.description = description

        notificationManager.createNotificationChannel(channel)

        builder = NotificationCompat.Builder(applicationContext, id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVibrate(LongArray(0))
            .build()

        return builder
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(id, createNotification())
    }
}