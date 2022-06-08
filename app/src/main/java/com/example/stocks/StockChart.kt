package com.example.stocks

import com.google.gson.annotations.SerializedName

data class StockChart(
    @SerializedName("date") var date: String? = null,
    @SerializedName("open") var open: Float? = null,
    @SerializedName("low") var low: Float? = null,
    @SerializedName("high") var high: Float? = null,
    @SerializedName("close") var close: Float? = null,
    @SerializedName("volume") var volume: Int?    = null
)
