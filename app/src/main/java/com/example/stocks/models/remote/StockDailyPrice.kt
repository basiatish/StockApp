package com.example.stocks.models.remote

import com.google.gson.annotations.SerializedName

data class StockDailyPrice(

    @SerializedName("date") var date: String? = null,
    @SerializedName("open") var open: Float? = null,
    @SerializedName("high") var high: Float? = null,
    @SerializedName("low") var low: Float? = null,
    @SerializedName("close") var close: Float? = null,
    @SerializedName("adjClose") var adjClose: String? = null,
    @SerializedName("volume") var volume: Float? = null,
    @SerializedName("unadjustedVolume") var unadjustedVolume : Int? = null,
    @SerializedName("change") var change: String? = null,
    @SerializedName("changePercent") var changePercent: String? = null,
    @SerializedName("vwap") var vwap: String? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("changeOverTime") var changeOverTime: String? = null

)
