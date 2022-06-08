package com.example.stocks

import com.google.gson.annotations.SerializedName

data class StockDividends(
    @SerializedName("date") var date: String? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("adjDividend") var adjDividend: String? = null,
    @SerializedName("dividend") var dividend: String? = null,
    @SerializedName("recordDate") var recordDate: String? = null,
    @SerializedName("paymentDate") var paymentDate: String? = null,
    @SerializedName("declarationDate") var declarationDate : String? = null
)
