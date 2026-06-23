package com.example.fittrackpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseName: String,
    val category: String,
    val sets: Int,
    val reps: Int,
    val weight: Double,
    val workoutDate: Long,
    val notes: String
)
