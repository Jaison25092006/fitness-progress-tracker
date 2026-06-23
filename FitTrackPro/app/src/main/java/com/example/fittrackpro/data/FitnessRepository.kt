package com.example.fittrackpro.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

interface FitnessRepository {
    val workoutsState: StateFlow<List<WorkoutEntity>>
    val measurementsState: StateFlow<List<BodyMeasurementEntity>>
    val progressPhotosState: StateFlow<List<ProgressPhotoEntity>>
    val nutritionLogsState: StateFlow<List<NutritionEntity>>

    suspend fun insertWorkout(workout: WorkoutEntity): Long
    suspend fun updateWorkout(workout: WorkoutEntity)
    suspend fun deleteWorkout(workout: WorkoutEntity)
    suspend fun deleteAllWorkouts()

    suspend fun insertMeasurement(measurement: BodyMeasurementEntity): Long
    suspend fun updateMeasurement(measurement: BodyMeasurementEntity)
    suspend fun deleteMeasurement(measurement: BodyMeasurementEntity)
    suspend fun deleteAllMeasurements()

    suspend fun insertProgressPhoto(photo: ProgressPhotoEntity): Long
    suspend fun updateProgressPhoto(photo: ProgressPhotoEntity)
    suspend fun deleteProgressPhoto(photo: ProgressPhotoEntity)
    suspend fun deleteAllProgressPhotos()

    suspend fun insertNutritionLog(log: NutritionEntity): Long
    suspend fun updateNutritionLog(log: NutritionEntity)
    suspend fun deleteNutritionLog(log: NutritionEntity)
    suspend fun deleteAllNutritionLogs()
}

class DefaultFitnessRepository @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val bodyMeasurementDao: BodyMeasurementDao,
    private val progressPhotoDao: ProgressPhotoDao,
    private val nutritionDao: NutritionDao,
    externalScope: CoroutineScope
) : FitnessRepository {

    override val workoutsState: StateFlow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()
        .stateIn(externalScope, SharingStarted.Eagerly, emptyList())

    override val measurementsState: StateFlow<List<BodyMeasurementEntity>> = bodyMeasurementDao.getAllMeasurements()
        .stateIn(externalScope, SharingStarted.Eagerly, emptyList())

    override val progressPhotosState: StateFlow<List<ProgressPhotoEntity>> = progressPhotoDao.getAllProgressPhotos()
        .stateIn(externalScope, SharingStarted.Eagerly, emptyList())

    override val nutritionLogsState: StateFlow<List<NutritionEntity>> = nutritionDao.getAllNutritionLogs()
        .stateIn(externalScope, SharingStarted.Eagerly, emptyList())

    override suspend fun insertWorkout(workout: WorkoutEntity): Long = workoutDao.insertWorkout(workout)
    override suspend fun updateWorkout(workout: WorkoutEntity) = workoutDao.updateWorkout(workout)
    override suspend fun deleteWorkout(workout: WorkoutEntity) = workoutDao.deleteWorkout(workout)
    override suspend fun deleteAllWorkouts() = workoutDao.deleteAllWorkouts()

    override suspend fun insertMeasurement(measurement: BodyMeasurementEntity): Long = bodyMeasurementDao.insertMeasurement(measurement)
    override suspend fun updateMeasurement(measurement: BodyMeasurementEntity) = bodyMeasurementDao.updateMeasurement(measurement)
    override suspend fun deleteMeasurement(measurement: BodyMeasurementEntity) = bodyMeasurementDao.deleteMeasurement(measurement)
    override suspend fun deleteAllMeasurements() = bodyMeasurementDao.deleteAllMeasurements()

    override suspend fun insertProgressPhoto(photo: ProgressPhotoEntity): Long = progressPhotoDao.insertProgressPhoto(photo)
    override suspend fun updateProgressPhoto(photo: ProgressPhotoEntity) = progressPhotoDao.updateProgressPhoto(photo)
    override suspend fun deleteProgressPhoto(photo: ProgressPhotoEntity) = progressPhotoDao.deleteProgressPhoto(photo)
    override suspend fun deleteAllProgressPhotos() = progressPhotoDao.deleteAllProgressPhotos()

    override suspend fun insertNutritionLog(log: NutritionEntity): Long = nutritionDao.insertNutritionLog(log)
    override suspend fun updateNutritionLog(log: NutritionEntity) = nutritionDao.updateNutritionLog(log)
    override suspend fun deleteNutritionLog(log: NutritionEntity) = nutritionDao.deleteNutritionLog(log)
    override suspend fun deleteAllNutritionLogs() = nutritionDao.deleteAllNutritionLogs()
}
