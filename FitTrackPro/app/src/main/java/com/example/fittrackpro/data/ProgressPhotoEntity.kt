package com.example.fittrackpro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress_photos")
data class ProgressPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imagePath: String,
    val date: Long,
    val notes: String
)
