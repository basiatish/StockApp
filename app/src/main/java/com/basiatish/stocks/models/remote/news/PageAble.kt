package com.basiatish.stocks.models.remote.news

import com.google.gson.annotations.SerializedName

data class PageAble(
    @SerializedName("sort") var sort: Sort? = null,
    @SerializedName("pageSize") var pageSize: Int? = null,
    @SerializedName("pageNumber") var pageNumber: Int? = null,
    @SerializedName("offset") var offset: Int? = null,
    @SerializedName("paged") var paged: Boolean? = null,
    @SerializedName("unpaged") var unpaged: Boolean? = null
)
