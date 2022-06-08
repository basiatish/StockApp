package com.example.stocks

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

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

    @GET("v3/historical-price-full/{name}")
    suspend fun getFullHistoryPrice(
        @Path(value = "name", encoded = true) compName: String,
        @Query(value = "serietype", encoded = true) time: String,
        @Query(value = "apikey", encoded = true) apiKey: String
    ): StockYearlyPriceHeader

    @GET("v3/profile/{name}")
    suspend fun getCompanyProfile(
        @Path(value = "name", encoded = true) compName: String,
        @Query(value = "apikey", encoded = true) apiKey: String
    ): MutableList<CompanyProfile>

    @GET("v3/historical-price-full/stock_dividend/{name}")
    suspend fun getCompanyDividends(
        @Path(value = "name", encoded = true) compName: String,
        @Query(value = "apikey", encoded = true) apiKey: String
    ): StockDividendsHeader
}

object StockApi {
    val retrofitService: StocksApiService by lazy { retrofit.create(StocksApiService::class.java) }
}