package com.example.fittrackpro

import android.app.Application
import com.example.fittrackpro.notifications.ReminderNotificationHelper
import com.example.fittrackpro.notifications.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FitTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Notification Channels
        ReminderNotificationHelper.createNotificationChannels(this)
        
        // Schedule WorkManager reminders
        ReminderScheduler.scheduleReminders(this)
    }
}
