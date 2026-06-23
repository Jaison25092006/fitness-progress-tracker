package com.example.fittrackpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurements")
data class BodyMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weight: Double,
    val chest: Double,
    val waist: Double,
    val arms: Double,
    val thighs: Double,
    val bodyFat: Double,
    val measurementDate: Long
)
