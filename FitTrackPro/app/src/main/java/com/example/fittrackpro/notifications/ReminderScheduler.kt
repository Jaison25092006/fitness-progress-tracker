package com.example.fittrackpro.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_WORKOUT = "workout_reminder_work"
    private const val WORK_WATER = "water_reminder_work"
    private const val WORK_MEASUREMENT = "measurement_reminder_work"
    private const val WORK_DAILY_GOAL = "daily_goal_reminder_work"

    fun scheduleReminders(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Simple default constraints (none really needed for offline local reminders, but good practice)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // 1. Workout Reminder: Every 24 hours
        val workoutRequest = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_WORKOUT,
            ExistingPeriodicWorkPolicy.KEEP,
            workoutRequest
        )

        // 2. Water Reminder: Every 4 hours
        val waterRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(4, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_WATER,
            ExistingPeriodicWorkPolicy.KEEP,
            waterRequest
        )

        // 3. Measurement Reminder: Every 7 days
        val measurementRequest = PeriodicWorkRequestBuilder<MeasurementReminderWorker>(7, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_MEASUREMENT,
            ExistingPeriodicWorkPolicy.KEEP,
            measurementRequest
        )

        // 4. Daily Goal Reminder: Every 24 hours
        val dailyGoalRequest = PeriodicWorkRequestBuilder<DailyGoalReminderWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_DAILY_GOAL,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyGoalRequest
        )
    }
}
