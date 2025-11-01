package com.example.my_kidney_app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Insert
    suspend fun insert(obj: Drink)
    @Query("""
        SELECT * FROM drink 
        WHERE date(datetime(timestamp / 1000, 'unixepoch', 'localtime')) = date('now', 'localtime')
        ORDER BY timestamp DESC  
    """)
    fun getTodayDrinks(): Flow<List<Drink>>
    @Query("DELETE FROM drink WHERE id = :id")
    suspend fun deleteById(id: Int)
}
