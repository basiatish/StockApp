package com.example.stocks

import com.google.gson.annotations.SerializedName

data class StockYearlyPrice(
    @SerializedName("date") var date: String? = null,
    @SerializedName("close") var close: Double? = null
)
