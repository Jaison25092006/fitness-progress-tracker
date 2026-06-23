package com.example.fittrackpro.ui.main

import android.content.SharedPreferences
import com.example.fittrackpro.data.AuthRepository
import com.example.fittrackpro.data.BodyMeasurementEntity
import com.example.fittrackpro.data.FitnessRepository
import com.example.fittrackpro.data.NutritionEntity
import com.example.fittrackpro.data.ProgressPhotoEntity
import com.example.fittrackpro.data.WorkoutEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {
    @Test
    fun testDefaultStateCalculations() = runTest {
        val fitnessRepository = FakeFitnessRepository()
        val authRepository = FakeAuthRepository()
        val sharedPrefs = FakeSharedPreferences()

        val viewModel = MainScreenViewModel(fitnessRepository, authRepository, sharedPrefs)

        // Verify default units is KG
        assertEquals("KG", viewModel.getWeightUnit())

        // Verify dashboard stats are initialized with default values
        val stats = viewModel.dashboardStats.first()
        assertEquals(0.0, stats.currentWeight)
        assertEquals(0.0, stats.weightChange)
        assertEquals(0, stats.totalWorkouts)
        assertEquals(0.0, stats.caloriesBurned)
        assertEquals(0, stats.recentActivities.size)

        // Verify nutrition stats are initialized
        val nutrition = viewModel.nutritionStats.first()
        assertEquals(0, nutrition.todayCalories)
        assertEquals(0.0, nutrition.todayProtein)
        assertEquals(0, nutrition.todayWaterMl)
    }
}

// -------------------------------------------------------------
// FAKES FOR UNIT TESTING
// -------------------------------------------------------------

private class FakeFitnessRepository : FitnessRepository {
    override val workoutsState = MutableStateFlow<List<WorkoutEntity>>(emptyList())
    override val measurementsState = MutableStateFlow<List<BodyMeasurementEntity>>(emptyList())
    override val progressPhotosState = MutableStateFlow<List<ProgressPhotoEntity>>(emptyList())
    override val nutritionLogsState = MutableStateFlow<List<NutritionEntity>>(emptyList())

    override suspend fun insertWorkout(workout: WorkoutEntity): Long = 0L
    override suspend fun updateWorkout(workout: WorkoutEntity) {}
    override suspend fun deleteWorkout(workout: WorkoutEntity) {}
    override suspend fun deleteAllWorkouts() {}

    override suspend fun insertMeasurement(measurement: BodyMeasurementEntity): Long = 0L
    override suspend fun updateMeasurement(measurement: BodyMeasurementEntity) {}
    override suspend fun deleteMeasurement(measurement: BodyMeasurementEntity) {}
    override suspend fun deleteAllMeasurements() {}

    override suspend fun insertProgressPhoto(photo: ProgressPhotoEntity): Long = 0L
    override suspend fun updateProgressPhoto(photo: ProgressPhotoEntity) {}
    override suspend fun deleteProgressPhoto(photo: ProgressPhotoEntity) {}
    override suspend fun deleteAllProgressPhotos() {}

    override suspend fun insertNutritionLog(log: NutritionEntity): Long = 0L
    override suspend fun updateNutritionLog(log: NutritionEntity) {}
    override suspend fun deleteNutritionLog(log: NutritionEntity) {}
    override suspend fun deleteAllNutritionLogs() {}
}

private class FakeAuthRepository : AuthRepository {
    override val currentUserState = MutableStateFlow<String?>(null)
    override val isUserLoggedIn: Boolean = false
    override suspend fun login(email: String, password: String): Result<String> = Result.failure(Exception("Mock Login"))
    override suspend fun register(email: String, password: String): Result<String> = Result.failure(Exception("Mock Register"))
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)
    override fun logout() {}
}

private class FakeSharedPreferences : SharedPreferences {
    private val map = mutableMapOf<String, Any?>()
    override fun getAll(): Map<String, *> = map
    override fun getString(key: String, defValue: String?): String? = map[key] as? String ?: defValue
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? = map[key] as? Set<String> ?: defValues
    override fun getInt(key: String, defValue: Int): Int = map[key] as? Int ?: defValue
    override fun getLong(key: String, defValue: Long): Long = map[key] as? Long ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = map[key] as? Float ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = map[key] as? Boolean ?: defValue
    override fun contains(key: String): Boolean = map.containsKey(key)
    override fun edit(): SharedPreferences.Editor = FakeEditor(map)
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    private class FakeEditor(val map: MutableMap<String, Any?>) : SharedPreferences.Editor {
        val temp = mutableMapOf<String, Any?>()
        override fun putString(key: String, value: String?): SharedPreferences.Editor = apply { temp[key] = value }
        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor = apply { temp[key] = values }
        override fun putInt(key: String, value: Int): SharedPreferences.Editor = apply { temp[key] = value }
        override fun putLong(key: String, value: Long): SharedPreferences.Editor = apply { temp[key] = value }
        override fun putFloat(key: String, value: Float): SharedPreferences.Editor = apply { temp[key] = value }
        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor = apply { temp[key] = value }
        override fun remove(key: String): SharedPreferences.Editor = apply { temp[key] = null }
        override fun clear(): SharedPreferences.Editor = apply { temp.clear() }
        override fun commit(): Boolean {
            map.putAll(temp)
            return true
        }
        override fun apply() {
            map.putAll(temp)
        }
    }
}
