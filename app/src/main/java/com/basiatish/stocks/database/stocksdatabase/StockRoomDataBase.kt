package com.basiatish.stocks.database.stocksdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Stock::class], version = 1, exportSchema = false)
abstract class StockRoomDataBase : RoomDatabase() {

    abstract fun stockDao(): StockDao

    companion object {
        @Volatile
        private var database: StockRoomDataBase? = null

        fun getDataBase(context: Context): StockRoomDataBase {
            return database ?: synchronized(this) {
                val dataBase = Room.databaseBuilder(
                    context.applicationContext,
                    StockRoomDataBase::class.java,
                    "Stock_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                database = dataBase
                dataBase
            }
        }
    }

}