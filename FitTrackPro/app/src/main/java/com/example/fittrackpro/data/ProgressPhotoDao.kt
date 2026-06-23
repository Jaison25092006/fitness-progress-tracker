package com.example.fittrackpro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressPhotoDao {
    @Query("SELECT * FROM progress_photos ORDER BY date DESC")
    fun getAllProgressPhotos(): Flow<List<ProgressPhotoEntity>>

    @Query("SELECT * FROM progress_photos WHERE id = :id")
    fun getProgressPhotoById(id: Long): Flow<ProgressPhotoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressPhoto(photo: ProgressPhotoEntity): Long

    @Update
    suspend fun updateProgressPhoto(photo: ProgressPhotoEntity)

    @Delete
    suspend fun deleteProgressPhoto(photo: ProgressPhotoEntity)

    @Query("DELETE FROM progress_photos")
    suspend fun deleteAllProgressPhotos()
}
