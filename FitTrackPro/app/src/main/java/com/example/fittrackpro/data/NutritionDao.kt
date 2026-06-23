package com.example.fittrackpro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {
    @Query("SELECT * FROM nutrition_logs ORDER BY date DESC")
    fun getAllNutritionLogs(): Flow<List<NutritionEntity>>

    @Query("SELECT * FROM nutrition_logs WHERE id = :id")
    fun getNutritionLogById(id: Long): Flow<NutritionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionLog(log: NutritionEntity): Long

    @Update
    suspend fun updateNutritionLog(log: NutritionEntity)

    @Delete
    suspend fun deleteNutritionLog(log: NutritionEntity)

    @Query("DELETE FROM nutrition_logs")
    suspend fun deleteAllNutritionLogs()
}
