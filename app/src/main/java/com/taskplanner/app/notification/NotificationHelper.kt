package com.taskplanner.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.taskplanner.app.ui.MainActivity
import java.util.Locale

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val hourlyChannel = NotificationChannel(
                CHANNEL_HOURLY,
                "Hourly Focus Check-In",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Sends reminders every hour from 6am to 11pm"
            }

            val eventChannel = NotificationChannel(
                CHANNEL_EVENTS,
                "Workshop & Webinar Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority 30-minute alerts before bootcamps, webinars, and workshops"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(listOf(hourlyChannel, eventChannel))
        }
    }

    fun showHourlyNotification(hour: Int, pendingTaskCount: Int = 0) {
        val formattedTime = String.format(Locale.getDefault(), "%02d:00", hour)
        val content = if (pendingTaskCount > 0) {
            "You have $pendingTaskCount pending item(s) for the hour."
        } else {
            "Plan your tasks and focus priorities for this hour."
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_HOUR", hour)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            hour,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_HOURLY)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Hourly Check-in ($formattedTime)")
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID_HOURLY_BASE + hour, notification)
    }

    fun showEventNotification(title: String, eventType: String, meetingLink: String?) {
        val builder = NotificationCompat.Builder(context, CHANNEL_EVENTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Starting in 30 Minutes: $title")
            .setContentText("Your $eventType session starts in 30 minutes.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (!meetingLink.isNullOrBlank()) {
            val linkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingLink))
            val pendingLinkIntent = PendingIntent.getActivity(
                context,
                title.hashCode(),
                linkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(android.R.drawable.ic_menu_slides, "Join Session", pendingLinkIntent)
        }

        notificationManager.notify(title.hashCode(), builder.build())
    }

    companion object {
        const val CHANNEL_HOURLY = "channel_hourly_planner"
        const val CHANNEL_EVENTS = "channel_event_reminders"
        private const val NOTIFICATION_ID_HOURLY_BASE = 2000
    }
}
