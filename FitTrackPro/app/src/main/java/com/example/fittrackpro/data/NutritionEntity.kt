package com.example.fittrackpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_logs")
data class NutritionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val waterMl: Int,
    val date: Long,
    val mealName: String
)
