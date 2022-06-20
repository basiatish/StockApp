package com.example.stocks.models.remote

import com.example.stocks.models.remote.StockDividends
import com.google.gson.annotations.SerializedName

data class StockDividendsHeader(
    @SerializedName("symbol") var symbol: String? = null,
    @SerializedName("historical") var historical: List<StockDividends> = listOf()
)
