package com.basiatish.stocks.models.remote

import com.google.gson.annotations.SerializedName

data class StockYearlyPriceHeader(
    @SerializedName("symbol") var symbol: String? = null,
    @SerializedName("historical") var historical: List<StockYearlyPrice> = listOf()
)
