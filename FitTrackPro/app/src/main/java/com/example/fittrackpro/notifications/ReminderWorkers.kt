package com.example.fittrackpro.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fittrackpro.data.FitnessDatabase
import kotlinx.coroutines.flow.first
import java.util.Calendar

class WorkoutReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            val database = FitnessDatabase.getDatabase(applicationContext)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val workouts = database.workoutDao().getAllWorkouts().first()
            val hasLoggedToday = workouts.any { it.workoutDate >= today }

            if (!hasLoggedToday) {
                ReminderNotificationHelper.showNotification(
                    applicationContext,
                    ReminderNotificationHelper.CHANNEL_REMINDERS,
                    101,
                    "Time to Work Out! 💪",
                    "Don't miss your session today. Keep up the momentum!"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Result.success()
    }
}

class WaterReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            val database = FitnessDatabase.getDatabase(applicationContext)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val logs = database.nutritionDao().getAllNutritionLogs().first()
            val todayWater = logs.filter { it.date >= today }.sumOf { it.waterMl }

            if (todayWater < 2500) {
                ReminderNotificationHelper.showNotification(
                    applicationContext,
                    ReminderNotificationHelper.CHANNEL_REMINDERS,
                    102,
                    "Stay Hydrated! 💧",
                    "You've logged $todayWater ml of water today. Goal is 2500 ml. Take a sip!"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Result.success()
    }
}

class MeasurementReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            ReminderNotificationHelper.showNotification(
                applicationContext,
                ReminderNotificationHelper.CHANNEL_REMINDERS,
                103,
                "Weekly Weight & Stats Check 📐",
                "It's time to log your body weight and measurements to visualize your progress!"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Result.success()
    }
}

class DailyGoalReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            val database = FitnessDatabase.getDatabase(applicationContext)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val logs = database.nutritionDao().getAllNutritionLogs().first()
            val todayLogs = logs.filter { it.date >= today }
            val todayCal = todayLogs.sumOf { it.calories }
            val todayWater = todayLogs.sumOf { it.waterMl }

            if (todayCal >= 2000 && todayWater >= 2500) {
                ReminderNotificationHelper.showNotification(
                    applicationContext,
                    ReminderNotificationHelper.CHANNEL_GOALS,
                    201,
                    "Daily Goal Achieved! 🏆",
                    "Fantastic! You hit both your calorie ($todayCal kcal) and water ($todayWater ml) goals today!"
                )
            } else {
                ReminderNotificationHelper.showNotification(
                    applicationContext,
                    ReminderNotificationHelper.CHANNEL_REMINDERS,
                    104,
                    "Wrap Up Your Day 📝",
                    "Ensure all your meals, water intake, and workouts are logged for today's summary."
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Result.success()
    }
}
