package com.example.stocks.database.alertdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "Company name")
    val compName: String,
    @ColumnInfo(name = "Alert price")
    val price: Double,
    @ColumnInfo(name = "Create time")
    val time: Long,
    @ColumnInfo(name = "Above")
    val above: Boolean,
    @ColumnInfo(name = "Active")
    val status: Boolean
)
