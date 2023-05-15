package com.example.stocks.database.alertdatabase

import androidx.room.*

@Dao
interface AlertDao {

    @Query("Select * from alert")
    suspend fun getAlerts() : List<Alert>

    @Query("Select * from alert where `Company name` = :compName")
    suspend fun getCompanyAlerts(compName: String) : List<Alert>

    @Query("Select * from alert where id = :id")
    suspend fun getAlert(id: Int) : Alert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: Alert): Long

    @Update
    suspend fun update(alert: Alert)

    @Delete
    suspend fun delete(alert: Alert)

    @Query("Delete from alert")
    suspend fun clearDataBase()
}