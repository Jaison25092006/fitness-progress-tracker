package com.example.fittrackpro.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fittrackpro.MainActivity
import com.example.fittrackpro.R

object ReminderNotificationHelper {
    const val CHANNEL_REMINDERS = "reminders_channel"
    const val CHANNEL_GOALS = "goals_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nameReminders = "Daily Reminders"
            val descReminders = "Workout, Water, and Measurement reminders"
            val importanceReminders = NotificationManager.IMPORTANCE_DEFAULT
            val channelReminders = NotificationChannel(CHANNEL_REMINDERS, nameReminders, importanceReminders).apply {
                description = descReminders
            }

            val nameGoals = "Goal Achievements"
            val descGoals = "Notifications when you reach your fitness goals"
            val importanceGoals = NotificationManager.IMPORTANCE_HIGH
            val channelGoals = NotificationChannel(CHANNEL_GOALS, nameGoals, importanceGoals).apply {
                description = descGoals
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelReminders)
            notificationManager.createNotificationChannel(channelGoals)
        }
    }

    fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        content: String
    ) {
        // Check POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Using android.R.drawable.ic_dialog_info as placeholder small icon
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(
                if (channelId == CHANNEL_GOALS) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}
