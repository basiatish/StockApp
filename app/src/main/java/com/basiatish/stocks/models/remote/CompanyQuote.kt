package com.basiatish.stocks.models.remote

import com.google.gson.annotations.SerializedName

data class CompanyQuote(
    @SerializedName("symbol") var symbol: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("price") var price: Double? = null,
    @SerializedName("changesPercentage") var changesPercentage: Double? = null,
    @SerializedName("change") var change: Double? = null,
    @SerializedName("dayLow") var dayLow: Double? = null,
    @SerializedName("dayHigh") var dayHigh: Double? = null,
    @SerializedName("yearHigh") var yearHigh: String? = null,
    @SerializedName("yearLow") var yearLow: String? = null,
    @SerializedName("marketCap") var marketCap: Double? = null,
    @SerializedName("priceAvg50") var priceAvg50: String? = null,
    @SerializedName("priceAvg200") var priceAvg200: String? = null,
    @SerializedName("volume") var volume: Double? = null,
    @SerializedName("avgVolume") var avgVolume: Double? = null,
    @SerializedName("exchange") var exchange: String? = null,
    @SerializedName("open") var open: String? = null,
    @SerializedName("previousClose") var previousClose: String? = null,
    @SerializedName("eps") var eps: Double? = null,
    @SerializedName("pe") var pe: Double? = null,
    @SerializedName("earningsAnnouncement") var earningsAnnouncement: String? = null,
    @SerializedName("sharesOutstanding") var sharesOutstanding: String? = null,
    @SerializedName("timestamp") var timestamp: String? = null
)
