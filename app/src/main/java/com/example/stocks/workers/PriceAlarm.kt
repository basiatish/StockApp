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

class PriceAlarm(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext,
    params) {

    private val apiKey = BuildConfig.API_KEY
    private var priceChartMH: MutableList<StockChart> = mutableListOf()
    private val targetPrice = 150
    private val timeCreated = Timestamp(1676973907).time
    private var message = ""

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {


//        setForeground(makeStatusNotification("Worker Started", applicationContext))
//        setForegroundAsync(createForegroundInfo("Worker Started"))
        message = "Worker Started"
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, createForegroundInfo())
        }

        //createForegroundInfo("Worker Started")
        //makeStatusNotification("Worker Started", applicationContext)

        return withContext(IO) {
            return@withContext try {
                delay(5000)
                priceChartMH = StockApi.retrofitService
                    .getChart("1min", "AAPL", apiKey)

                for (item in priceChartMH.reversed()) {
                    val strDate = LocalDateTime.parse(item.date!!,
                            ofPattern("yyyy-MM-dd HH:mm:ss"))
                    val timestamp = LocalDateTime.of(strDate.year, strDate.monthValue,
                        strDate.dayOfMonth, strDate.hour, strDate.minute, strDate.second).
                        toEpochSecond(ZoneOffset.UTC)
                    if (timestamp > timeCreated) {
                        if (item.close!! > targetPrice) {
                            message = "Target Price reached at ${item.date}" +
                                    "!!! Current price is ${item.close}"
                            with(NotificationManagerCompat.from(applicationContext)) {
                                notify(1, createForegroundInfo())
                            }
//                            createForegroundInfo("Target Price reached at ${item.date}" +
//                                    "!!! Current price is ${item.close}")
//                            setForegroundAsync(createForegroundInfo("Target Price reached at ${item.date}" +
//                                    "!!! Current price is ${item.close}"))
//                            setForeground(makeStatusNotification("Target Price reached at ${item.date}" +
//                                    "!!! Current price is ${item.close}",
//                                applicationContext))
                            break
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

    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(1, createForegroundInfo())
    }

    private fun createForegroundInfo() : Notification {
        val id = "1225"
        val title = "WorkRequest Starting"

        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
        channel.description = description

        // Add the channel
        notificationManager.createNotificationChannel(channel)


        val builder = NotificationCompat.Builder(applicationContext, id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))


        //notificationManager.notify(1, builder.build())
        return builder.build()
    }

}