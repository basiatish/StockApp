package com.example.stocks.models.remote

import com.example.stocks.models.remote.StockDailyPrice
import com.google.gson.annotations.SerializedName

data class StockDailyPriceHeader(
    @SerializedName("symbol") var symbol: String? = null,
    @SerializedName("historical") var historical: List<StockDailyPrice> = listOf()
)