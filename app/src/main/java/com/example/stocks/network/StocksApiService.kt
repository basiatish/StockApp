package com.example.stocks.network

import com.example.stocks.models.remote.*
import com.example.stocks.models.remote.news.NewsContentHeader
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://financialmodelingprep.com/api/"

val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

val client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface StocksApiService {

    @GET("v3/search")
    suspend fun getCompanies(
        @Query("query", encoded = true) compName: String,
        @Query("limit", encoded = true) limit: String,
        @Query("exchange", encoded = true) exchange: String,
        @Query("apikey", encoded = true) apiKey: String
    ): List<StockSearch>

    @GET("v3/historical-chart/{time}/{name}")
    suspend fun getChart(
        @Path(value = "time", encoded = true) time: String,
        @Path(value = "name", encoded = true) compName: String,
        @Query(value = "apikey", encoded = true) apiKey: String
    ): MutableList<StockChart>

    @GET("v3/historical-price-full/{name}")
    suspend fun getFullHistoryDailyPrice(
        @Path(value = "name", encoded = true) compName: String,
        @Query(value = "apikey", encoded = true) apiKey: String
    ): StockDailyPriceHeader

    @GET("v3/profile/{name}")
    suspend fun getCompanyProfile(
        @Path(value = "name", encoded = true) compName: String,
        @Query(value = "apikey", encoded = true) apiKey: String
    ): MutableList<CompanyProfile>

    @GET("v3/quote/{name}")
    suspend fun getCompanyQuote(
        @Path(value = "name", encoded = true) compName: String,
        @Query(value = "apikey", encoded = true) apiKey: String
    ) : MutableList<CompanyQuote>

    @GET("v3/historical-price-full/stock_dividend/{name}")
    suspend fun getCompanyDividends(
        @Path(value = "name", encoded = true) compName: String,
        @Query(value = "apikey", encoded = true) apiKey: String
    ): StockDividendsHeader

    @GET("v3/fmp/articles")
    suspend fun getNews(
        @Query(value = "page", encoded = true) page: Int,
        @Query(value = "size", encoded = true) size: Int,
        @Query(value = "apikey", encoded = true) apiKey: String
    ) : NewsContentHeader
}

object StockApi {
    val retrofitService: StocksApiService by lazy { retrofit.create(StocksApiService::class.java) }
}