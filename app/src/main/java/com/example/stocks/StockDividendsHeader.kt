package com.example.stocks

import com.google.gson.annotations.SerializedName

data class StockDividendsHeader(
    @SerializedName("symbol") var symbol: String? = null,
    @SerializedName("historical") var historical: List<StockDividends> = listOf()
)
