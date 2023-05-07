package com.example.stocks.models.remote.news

import com.google.gson.annotations.SerializedName

data class NewsContentHeader(
    @SerializedName("content") var content: List<NewsContent> = listOf(),
    @SerializedName("pageable") var pageable: PageAble? = null,
    @SerializedName("totalPages") var totalPages: Int? = null,
    @SerializedName("totalElements") var totalElements: Int? = null,
    @SerializedName("last") var last: Boolean? = null,
    @SerializedName("number") var number: Int? = null,
    @SerializedName("size") var size: Int? = null,
    @SerializedName("numberOfElements") var numberOfElements: Int? = null,
    @SerializedName("sort") var sort: Sort? = null,
    @SerializedName("first") var first: Boolean? = null,
    @SerializedName("empty") var empty: Boolean? = null
)
