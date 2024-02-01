package com.basiatish.stocks.models.remote.news

import com.google.gson.annotations.SerializedName

data class Sort(
    @SerializedName("sorted") var sorted: Boolean? = null,
    @SerializedName("unsorted") var unsorted: Boolean? = null,
    @SerializedName("empty") var empty: Boolean? = null
)
