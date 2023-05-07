package com.example.stocks.models.remote.news

import com.google.gson.annotations.SerializedName

data class NewsContent(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("title") var title: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("tickers") var tickers: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("link") var link: String? = null,
    @SerializedName("author") var author: String? = null,
    @SerializedName("site") var site: String? = null
)
