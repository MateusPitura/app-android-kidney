package com.example.my_kidney_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink")
data class Drink(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val milliliters: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
