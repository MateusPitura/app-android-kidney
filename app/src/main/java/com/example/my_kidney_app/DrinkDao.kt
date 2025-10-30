package com.example.my_kidney_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Insert
    suspend fun insert(obj: Drink)
    @Query("SELECT * FROM drink ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Drink>>
}
