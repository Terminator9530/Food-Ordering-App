package com.example.foodorderingapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class DishEntity(
    @PrimaryKey val dish_id: Int,
    @ColumnInfo(name = "dish_name") val restaurantName: String,
    @ColumnInfo(name = "dish_price") val restaurantPrice: String,
    @ColumnInfo(name = "restaurant_id") val restaurantId: Int
)
