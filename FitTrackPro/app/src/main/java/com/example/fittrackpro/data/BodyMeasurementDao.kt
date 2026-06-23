package com.example.fittrackpro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {
    @Query("SELECT * FROM body_measurements ORDER BY measurementDate DESC")
    fun getAllMeasurements(): Flow<List<BodyMeasurementEntity>>

    @Query("SELECT * FROM body_measurements WHERE id = :id")
    fun getMeasurementById(id: Long): Flow<BodyMeasurementEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: BodyMeasurementEntity): Long

    @Update
    suspend fun updateMeasurement(measurement: BodyMeasurementEntity)

    @Delete
    suspend fun deleteMeasurement(measurement: BodyMeasurementEntity)

    @Query("DELETE FROM body_measurements")
    suspend fun deleteAllMeasurements()
}
