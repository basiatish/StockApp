package com.example.stocks.database.stocksdatabase

import androidx.room.*

@Dao
interface StockDao {

    @Query("Select * from stock")
    suspend fun getStocks() : List<Stock>

    @Query("Select stock.Short_name from stock where stock.Short_name = :shortName")
    suspend fun isExist(shortName: String) : String?

    @Query("Delete from stock where stock.Short_name = :shortName")
    suspend fun deleteByName(shortName: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: Stock)

    @Update
    suspend fun update(stock: Stock)

    @Delete
    suspend fun delete(stock: Stock)

    @Query("Delete from stock")
    suspend fun clearDatabase()
}