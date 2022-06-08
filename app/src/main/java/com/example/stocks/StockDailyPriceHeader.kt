package com.example.stocks

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class StockDailyPriceHeader(
    @SerializedName("symbol") var symbol: String? = null,
    @SerializedName("historical") var historical: List<StockDailyPrice> = listOf()
)
