package com.example.stocks.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stocks.BuildConfig
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
    private val targetPrice = 154
    private val timeCreated = Timestamp(1676212097).time

    override suspend fun doWork(): Result {

        makeStatusNotification("Worker Started", applicationContext)

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
                            makeStatusNotification("Target Price reached at ${item.date}" +
                                    "!!! Current price is ${item.close}",
                                applicationContext)
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

}