package com.basiatish.stocks.database.alertdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Alert::class], version = 1, exportSchema = false)
abstract class AlertRoomDataBase : RoomDatabase() {

    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var database: AlertRoomDataBase? = null

        fun getDataBase(context: Context): AlertRoomDataBase {
            return database ?: synchronized(this) {
                val dataBase = Room.databaseBuilder(
                    context.applicationContext,
                    AlertRoomDataBase::class.java,
                    "alert_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                database = dataBase
                dataBase
            }
        }
    }
}