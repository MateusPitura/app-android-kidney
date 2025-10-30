package com.example.my_kidney_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Drink::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun drinkDao(): DrinkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null
        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}