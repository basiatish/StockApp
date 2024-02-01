package com.basiatish.stocks.models.remote

import com.google.gson.annotations.SerializedName

data class StockSearch(
    @SerializedName("symbol") var symbol: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("currency") var currency: String? = null,
    @SerializedName("stockExchange") var stockExchange: String? = null,
    @SerializedName("exchangeShortName") var exchangeShortName: String? = null,
    @SerializedName("favorite") var favorite: Boolean = false
)
