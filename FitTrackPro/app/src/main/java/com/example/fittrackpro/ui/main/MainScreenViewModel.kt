package com.example.fittrackpro.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackpro.data.AuthRepository
import com.example.fittrackpro.data.BodyMeasurementEntity
import com.example.fittrackpro.data.FitnessRepository
import com.example.fittrackpro.data.NutritionEntity
import com.example.fittrackpro.data.ProgressPhotoEntity
import com.example.fittrackpro.data.WorkoutEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

// Statistical data structures
data class DashboardStats(
    val currentWeight: Double = 0.0,
    val weightChange: Double = 0.0,
    val totalWorkouts: Int = 0,
    val weeklyProgressCount: Int = 0,
    val caloriesBurned: Double = 0.0,
    val recentActivities: List<RecentActivity> = emptyList()
)

data class RecentActivity(
    val id: Long,
    val title: String,
    val subtitle: String,
    val date: Long,
    val iconType: String // "workout", "measurement", "nutrition", "photo"
)

data class NutritionStats(
    val todayCalories: Int = 0,
    val todayProtein: Double = 0.0,
    val todayCarbs: Double = 0.0,
    val todayFat: Double = 0.0,
    val todayWaterMl: Int = 0,
    val targetCalories: Int = 2000,
    val targetProtein: Double = 150.0,
    val targetCarbs: Double = 250.0,
    val targetFat: Double = 70.0,
    val targetWaterMl: Int = 2500
)

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val fitnessRepository: FitnessRepository,
    private val authRepository: AuthRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val currentUser = authRepository.currentUserState

    // 1. Core database flows exposed as state flows
    val workoutsState: StateFlow<List<WorkoutEntity>> = fitnessRepository.workoutsState
    val measurementsState: StateFlow<List<BodyMeasurementEntity>> = fitnessRepository.measurementsState
    val progressPhotosState: StateFlow<List<ProgressPhotoEntity>> = fitnessRepository.progressPhotosState
    val nutritionLogsState: StateFlow<List<NutritionEntity>> = fitnessRepository.nutritionLogsState

    // Expose unit preference ("KG" or "LBS") dynamically
    fun getWeightUnit(): String {
        return sharedPreferences.getString("unit_preference", "KG") ?: "KG"
    }

    // 2. Dynamic dashboard statistics compiler
    val dashboardStats: StateFlow<DashboardStats> = combine(
        workoutsState,
        measurementsState,
        nutritionLogsState,
        progressPhotosState
    ) { workouts, measurements, nutrition, photos ->
        // Latest Weight
        val currentWeight = if (measurements.isNotEmpty()) measurements.first().weight else 0.0
        // Weight Change (latest - oldest)
        val oldestWeight = if (measurements.isNotEmpty()) measurements.last().weight else 0.0
        val weightChange = if (measurements.isNotEmpty()) currentWeight - oldestWeight else 0.0

        val totalWorkouts = workouts.size

        // Workouts in the last 7 days
        val sevenDaysAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.timeInMillis
        val weeklyProgressCount = workouts.count { it.workoutDate >= sevenDaysAgo }

        // Est Calories Burned: sets * reps * weight * 0.05 kcal
        val caloriesBurned = workouts.sumOf { it.sets * it.reps * it.weight * 0.05 }

        // Feed of recent activity: combine all logs and take latest 5
        val activitiesList = mutableListOf<RecentActivity>()
        workouts.take(5).forEach {
            activitiesList.add(RecentActivity(it.id, it.exerciseName, "${it.sets} sets x ${it.reps} reps (${it.weight} ${getWeightUnit()})", it.workoutDate, "workout"))
        }
        measurements.take(5).forEach {
            activitiesList.add(RecentActivity(it.id, "Body Stats Logged", "Weight: ${it.weight} ${getWeightUnit()} (Body Fat: ${it.bodyFat}%)", it.measurementDate, "measurement"))
        }
        nutrition.take(5).forEach {
            val subtitle = if (it.mealName == "Water Intake") "${it.waterMl} ml Hydration" else "${it.calories} kcal (P: ${it.protein.toInt()}g, C: ${it.carbs.toInt()}g, F: ${it.fat.toInt()}g)"
            activitiesList.add(RecentActivity(it.id, it.mealName, subtitle, it.date, "nutrition"))
        }
        photos.take(5).forEach {
            activitiesList.add(RecentActivity(it.id, "Progress Photo Added", it.notes.ifBlank { "Logged monthly progress photo" }, it.date, "photo"))
        }
        val recentActivities = activitiesList.sortedByDescending { it.date }.take(5)

        DashboardStats(
            currentWeight = currentWeight,
            weightChange = weightChange,
            totalWorkouts = totalWorkouts,
            weeklyProgressCount = weeklyProgressCount,
            caloriesBurned = caloriesBurned,
            recentActivities = recentActivities
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // 3. Dynamic nutrition macro tracker compiler
    val nutritionStats: StateFlow<NutritionStats> = nutritionLogsState.map { logs ->
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayLogs = logs.filter { it.date >= todayStart }
        val calories = todayLogs.sumOf { it.calories }
        val protein = todayLogs.sumOf { it.protein }
        val carbs = todayLogs.sumOf { it.carbs }
        val fat = todayLogs.sumOf { it.fat }
        val water = todayLogs.sumOf { it.waterMl }

        NutritionStats(
            todayCalories = calories,
            todayProtein = protein,
            todayCarbs = carbs,
            todayFat = fat,
            todayWaterMl = water
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NutritionStats())

    // 4. Suspend methods forwarding CRUD requests to background dispatcher
    fun insertWorkout(workout: WorkoutEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.insertWorkout(workout)
        }
    }

    fun updateWorkout(workout: WorkoutEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.updateWorkout(workout)
        }
    }

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.deleteWorkout(workout)
        }
    }

    fun insertMeasurement(measurement: BodyMeasurementEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.insertMeasurement(measurement)
        }
    }

    fun updateMeasurement(measurement: BodyMeasurementEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.updateMeasurement(measurement)
        }
    }

    fun deleteMeasurement(measurement: BodyMeasurementEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.deleteMeasurement(measurement)
        }
    }

    fun insertNutritionLog(log: NutritionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.insertNutritionLog(log)
        }
    }

    fun deleteNutritionLog(log: NutritionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.deleteNutritionLog(log)
        }
    }

    fun insertProgressPhoto(photo: ProgressPhotoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.insertProgressPhoto(photo)
        }
    }

    fun deleteProgressPhoto(photo: ProgressPhotoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            fitnessRepository.deleteProgressPhoto(photo)
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
