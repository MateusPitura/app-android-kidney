package com.example.my_kidney_app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Insert
    suspend fun insert(obj: Drink) // suspend retorna um único valor
    @Query("""
        SELECT * FROM drink 
        WHERE date(datetime(timestamp / 1000, 'unixepoch', 'localtime')) = date('now', 'localtime')
        ORDER BY timestamp DESC  
    """) // Converte ms para seconds, depois para o formato de data e filtra pela data de hoje
    fun getTodayDrinks(): Flow<List<Drink>> // Flow retorna um stream de dados a medida que chegam
    @Query("""
        SELECT SUM(milliliters) FROM drink 
        WHERE date(datetime(timestamp / 1000, 'unixepoch', 'localtime')) = date('now', 'localtime')
    """) // Soma o total tomado hoje
    fun getTodayAmount(): Flow<Int?>
    @Query("DELETE FROM drink WHERE id = :id")
    suspend fun deleteById(id: Int)
    @Update
    suspend fun update(drink: Drink)
}
