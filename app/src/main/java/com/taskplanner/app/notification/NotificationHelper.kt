package com.taskplanner.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.taskplanner.app.R
import com.taskplanner.app.ui.MainActivity

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remindersChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for hourly and event reminders"
            }

            val syncChannel = NotificationChannel(
                CHANNEL_ID_SYNC,
                "Gmail Sync",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for Gmail synchronization"
            }

            notificationManager.createNotificationChannel(remindersChannel)
            notificationManager.createNotificationChannel(syncChannel)
        }
    }

    fun showHourlyReminderNotification() {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            HOURLY_REMINDER_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Task Planner Reminder")
            .setContentText("Time to check your tasks!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(HOURLY_REMINDER_NOTIFICATION_ID, notification)
    }

    fun showEventReminderNotification(title: String = "Event Reminder", message: String = "You have an upcoming event") {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            EVENT_REMINDER_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(EVENT_REMINDER_NOTIFICATION_ID, notification)
    }

    fun showGmailSyncNotification(message: String = "Gmail sync in progress...") {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SYNC)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Gmail Sync")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(GMAIL_SYNC_NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID_REMINDERS = "task_reminders"
        private const val CHANNEL_ID_SYNC = "gmail_sync"
        private const val HOURLY_REMINDER_NOTIFICATION_ID = 1
        private const val EVENT_REMINDER_NOTIFICATION_ID = 2
        private const val GMAIL_SYNC_NOTIFICATION_ID = 3
    }
}
