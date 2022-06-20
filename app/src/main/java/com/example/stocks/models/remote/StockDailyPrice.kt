package com.example.stocks.models.remote

import com.google.gson.annotations.SerializedName

data class StockDailyPrice(

    @SerializedName("date") var date: String? = null,
    @SerializedName("open") var open: Double? = null,
    @SerializedName("high") var high: Double? = null,
    @SerializedName("low") var low: Double? = null,
    @SerializedName("close") var close: Double? = null,
    @SerializedName("adjClose") var adjClose: Double? = null,
    @SerializedName("volume") var volume: Int? = null,
    @SerializedName("unadjustedVolume") var unadjustedVolume : Int? = null,
    @SerializedName("change") var change: Double? = null,
    @SerializedName("changePercent") var changePercent: Double? = null,
    @SerializedName("vwap") var vwap: Double? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("changeOverTime") var changeOverTime: Double? = null

)
