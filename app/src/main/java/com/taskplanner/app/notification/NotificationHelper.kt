package com.taskplanner.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.taskplanner.app.data.model.TaskItem
import com.taskplanner.app.receiver.AlarmReceiver
import com.taskplanner.app.ui.MainActivity
import java.util.Locale

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val hourlyChannel = NotificationChannel(
                CHANNEL_HOURLY,
                "ChronoDo Hourly Digest",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recurring hourly check-ins and pending task digests"
                enableVibration(true)
            }

            val eventChannel = NotificationChannel(
                CHANNEL_EVENTS,
                "ChronoDo Workshop Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority 30-minute alerts before bootcamps, webinars, and workshops"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(listOf(hourlyChannel, eventChannel))
        }
    }

    /**
     * Displays the hourly digest notification.
     * Fatigue Management (PRD §6): Automatically suppressed if pendingTaskCount == 0.
     */
    fun showHourlyNotification(hour: Int, pendingTasks: List<TaskItem>) {
        // Notification Fatigue Management: Suppress if no pending items
        if (pendingTasks.isEmpty()) {
            cancelHourlyNotification(hour)
            return
        }

        val formattedTime = String.format(Locale.getDefault(), "%02d:00", hour)
        val pendingCount = pendingTasks.size
        val topTask = pendingTasks.firstOrNull()

        val contentTitle = "ChronoDo • $pendingCount Pending ($formattedTime)"
        val contentText = if (topTask != null) {
            "Top priority: ${topTask.title}"
        } else {
            "You have $pendingCount open action items."
        }

        // Tap notification to open app
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_HOUR", hour)
        }
        val pendingOpenIntent = PendingIntent.getActivity(
            context,
            hour,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_HOURLY)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(pendingOpenIntent)
            .setNumber(pendingCount)
            .setSubText("$pendingCount active")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Multi-line inbox style for remaining tasks
        if (pendingTasks.size > 1) {
            val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle("Active Hourly Tasks ($pendingCount)")
            pendingTasks.take(5).forEach { task ->
                val pPrefix = when (task.priority) {
                    com.taskplanner.app.data.model.TaskPriority.HIGH -> "⚡ "
                    com.taskplanner.app.data.model.TaskPriority.MEDIUM -> "• "
                    com.taskplanner.app.data.model.TaskPriority.LOW -> "◦ "
                }
                inboxStyle.addLine("$pPrefix${task.title}")
            }
            builder.setStyle(inboxStyle)
        }

        // Direct Inline Action 1: Mark Done (Top Task)
        if (topTask != null) {
            val markDoneIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmReceiver.ACTION_MARK_DONE
                putExtra(AlarmReceiver.EXTRA_TASK_ID, topTask.id)
                putExtra(AlarmReceiver.EXTRA_SCHEDULED_HOUR, hour)
            }
            val pendingMarkDone = PendingIntent.getBroadcast(
                context,
                (topTask.id + 10000).toInt(),
                markDoneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(android.R.drawable.checkbox_on_background, "✓ Mark Done", pendingMarkDone)
        }

        // Direct Inline Action 2: Snooze 1 Hour
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_SNOOZE
            putExtra(AlarmReceiver.EXTRA_SCHEDULED_HOUR, hour)
        }
        val pendingSnooze = PendingIntent.getBroadcast(
            context,
            hour + 5000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(android.R.drawable.ic_menu_recent_history, "⏰ Snooze 1h", pendingSnooze)

        notificationManager.notify(NOTIFICATION_ID_HOURLY_BASE, builder.build())
    }

    fun cancelHourlyNotification(hour: Int = 0) {
        notificationManager.cancel(NOTIFICATION_ID_HOURLY_BASE)
    }

    fun showEndOfDayNotification(pendingTasks: List<TaskItem>) {
        if (pendingTasks.isEmpty()) return

        val contentTitle = "ChronoDo • End of Day Snapshot"
        val contentText = "You have ${pendingTasks.size} action items remaining."

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingOpenIntent = PendingIntent.getActivity(
            context,
            9999,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_HOURLY)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(pendingOpenIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (pendingTasks.size > 1) {
            val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle("End of Day Snapshot (${pendingTasks.size})")
            pendingTasks.take(5).forEach { task ->
                val pPrefix = when (task.priority) {
                    com.taskplanner.app.data.model.TaskPriority.HIGH -> "⚡ "
                    com.taskplanner.app.data.model.TaskPriority.MEDIUM -> "• "
                    com.taskplanner.app.data.model.TaskPriority.LOW -> "◦ "
                }
                inboxStyle.addLine("$pPrefix${task.title}")
            }
            builder.setStyle(inboxStyle)
        }

        notificationManager.notify(NOTIFICATION_ID_EOD, builder.build())
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
            builder.addAction(android.R.drawable.ic_menu_view, "Join Session", pendingLinkIntent)
        }

        notificationManager.notify(title.hashCode(), builder.build())
    }

    companion object {
        const val CHANNEL_HOURLY = "channel_hourly_planner"
        const val CHANNEL_EVENTS = "channel_event_reminders"
        private const val NOTIFICATION_ID_HOURLY_BASE = 2000
        private const val NOTIFICATION_ID_EOD = 3000
    }
}
