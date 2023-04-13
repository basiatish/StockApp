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
import com.example.stocks.BuildConfig
import com.example.stocks.R
import com.example.stocks.StockApi
import com.example.stocks.models.remote.StockChart
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter.ofPattern

private const val TAG = "PriceAlarm"

class PriceAlarmWork(appContext: Context, params: WorkerParameters) : CoroutineWorker(
    appContext,
    params
) {

    private lateinit var builder: Notification

    private val apiKey = BuildConfig.API_KEY
    private var priceChartMH: MutableList<StockChart> = mutableListOf()
    private var targetPrice: Double? = null
    private var timeCreated: Long? = null
    private var compName: String? = null
    private var above: Boolean = false

    private var tag = params.tags.elementAt(0)
    private var message = ""

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {

        compName = inputData.getString("name")
        targetPrice = inputData.getDouble("price", 150.0)
        timeCreated = inputData.getLong("time", System.currentTimeMillis())
        above = inputData.getBoolean("above", false)


        message = "Worker Started"
//        with(NotificationManagerCompat.from(applicationContext)) {
//            notify(1, builder)
//        }
        setForegroundAsync(createForegroundInfo())
        setForeground(createForegroundInfo())
        notificationManager.notify(1, builder)
        delay(10000)
//        return withContext(IO) {
//            return@withContext try {
////                priceChartMH = StockApi.retrofitService
////                    .getChart("1hour", compName!!, apiKey)
//
//                for (item in priceChartMH.reversed()) {
//                    val strDate = LocalDateTime.parse(
//                        item.date!!,
//                        ofPattern("yyyy-MM-dd HH:mm:ss")
//                    )
//                    val timestamp = LocalDateTime.of(
//                        strDate.year, strDate.monthValue,
//                        strDate.dayOfMonth, strDate.hour, strDate.minute, strDate.second
//                    ).toEpochSecond(ZoneOffset.UTC)
//                    if (timestamp > timeCreated!!) {
//                        if (above) {
//                            if (item.close!! > targetPrice!!) {
//                                message = "Price is above $targetPrice\n" +
//                                        "Time: ${item.date}\n" +
//                                        "Current price is ${item.close}"
//                                setForeground(createForegroundInfo())
////                                with(NotificationManagerCompat.from(applicationContext)) {
////                                    notify(1, createForegroundInfo())
////                                }
//                                WorkManager.getInstance(applicationContext).cancelAllWorkByTag(tag)
//                                break
//                            }
//                        } else {
//                            if (item.close!! < targetPrice!!) {
//                                message = "Price is below $targetPrice\n" +
//                                        "Time: ${item.date}\n" +
//                                        "Current price is ${item.close}"
//                                setForeground(createForegroundInfo())
////                                with(NotificationManagerCompat.from(applicationContext)) {
////                                    notify(1, createForegroundInfo())
////                                }
//                                WorkManager.getInstance(applicationContext).cancelAllWorkByTag(tag)
//                                break
//                            }
//                        }
//                    }
//                }
                Log.e(TAG, "Success")
                 return Result.success()
//            } catch (th: Throwable) {
//                Log.e(TAG, "Error $th")
//                Result.failure()
//            }
        //}

    }

//    override suspend fun getForegroundInfo(): ForegroundInfo {
//        return ForegroundInfo(1, createForegroundInfo())
//    }

    private fun createForegroundInfo(): ForegroundInfo {
        val id = "1225"
        val title = "WorkRequest Starting"

        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
        channel.description = description

        // Add the channel
        notificationManager.createNotificationChannel(channel)

        builder = NotificationCompat.Builder(applicationContext, id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVibrate(LongArray(0))
            .build()

        //notificationManager.notify(1, builder.build())
        return ForegroundInfo(1, builder)
    }

//    private fun createForegroundInfo(): Notification {
//        val id = "1225"
//        val title = "WorkRequest Starting"
//
//        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
//        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
//        val importance = NotificationManager.IMPORTANCE_HIGH
//        val channel = NotificationChannel(id, name, importance)
//        channel.description = description
//
//        // Add the channel
//        notificationManager.createNotificationChannel(channel)
//
//        val builder = NotificationCompat.Builder(applicationContext, id)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setVibrate(LongArray(0))
//
//        //notificationManager.notify(1, builder.build())
//        return builder.build()
//    }

}