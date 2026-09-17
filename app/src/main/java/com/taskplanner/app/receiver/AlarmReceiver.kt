package com.taskplanner.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taskplanner.app.alarm.AlarmScheduler
import com.taskplanner.app.data.local.AppDatabase
import com.taskplanner.app.data.model.TaskStatus
import com.taskplanner.app.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        val scheduler = AlarmScheduler(context)

        when (intent.action) {
            ACTION_HOURLY_REMINDER -> {
                val hour = intent.getIntExtra(EXTRA_SCHEDULED_HOUR, -1)
                val startHour = intent.getIntExtra(EXTRA_START_HOUR, 8)
                val endHour = intent.getIntExtra(EXTRA_END_HOUR, 20)

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getInstance(context)
                        val pendingTasks = db.plannerDao().getAllPendingTasks()

                        // Filter by frequency
                        val hourlyAlertTasks = pendingTasks.filter { task ->
                            when (task.notificationFrequency) {
                                com.taskplanner.app.data.model.NotificationFrequency.NONE -> false
                                com.taskplanner.app.data.model.NotificationFrequency.ONCE -> task.scheduledHour == hour
                                com.taskplanner.app.data.model.NotificationFrequency.HOURLY -> true
                                com.taskplanner.app.data.model.NotificationFrequency.DAILY -> false
                            }
                        }

                        // Fatigue management handled inside showHourlyNotification (suppressed if empty)
                        notificationHelper.showHourlyNotification(hour, hourlyAlertTasks)
                    } finally {
                        pendingResult.finish()
                    }
                }

                // Chain the next hourly alarm within active window
                scheduler.scheduleNextHourlyReminder(startHour, endHour)
            }

            ACTION_MARK_DONE -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
                val hour = intent.getIntExtra(EXTRA_SCHEDULED_HOUR, -1)

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getInstance(context)
                        if (taskId != -1L) {
                            val task = db.plannerDao().getTaskById(taskId)
                            if (task != null) {
                                val updated = task.copy(
                                    status = TaskStatus.COMPLETED,
                                    isCompleted = true,
                                    completedAt = System.currentTimeMillis()
                                )
                                db.plannerDao().updateTask(updated)
                            }
                        }

                        // Re-fetch pending tasks & refresh notification
                        val remainingPending = db.plannerDao().getAllPendingTasks()
                        notificationHelper.showHourlyNotification(hour, remainingPending)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            ACTION_SNOOZE -> {
                val hour = intent.getIntExtra(EXTRA_SCHEDULED_HOUR, -1)
                notificationHelper.cancelHourlyNotification(hour)
                scheduler.snoozeHourlyReminder(60)
            }

            ACTION_EVENT_REMINDER -> {
                val title = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Upcoming Event"
                val type = intent.getStringExtra(EXTRA_EVENT_TYPE) ?: "Workshop/Webinar"
                val link = intent.getStringExtra(EXTRA_MEETING_LINK)
                notificationHelper.showEventNotification(title, type, link)
            }

            ACTION_END_OF_DAY_REMINDER -> {
                val endHour = intent.getIntExtra(EXTRA_END_HOUR, 20)
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getInstance(context)
                        val pendingTasks = db.plannerDao().getAllPendingTasks()
                        val eodTasks = pendingTasks.filter { 
                            it.notificationFrequency != com.taskplanner.app.data.model.NotificationFrequency.NONE 
                        }
                        notificationHelper.showEndOfDayNotification(eodTasks)
                    } finally {
                        pendingResult.finish()
                    }
                }
                scheduler.scheduleEndOfDayReminder(endHour)
            }
        }
    }

    companion object {
        const val ACTION_HOURLY_REMINDER = "com.taskplanner.ACTION_HOURLY_REMINDER"
        const val ACTION_EVENT_REMINDER = "com.taskplanner.ACTION_EVENT_REMINDER"
        const val ACTION_MARK_DONE = "com.taskplanner.ACTION_MARK_DONE"
        const val ACTION_SNOOZE = "com.taskplanner.ACTION_SNOOZE"
        const val ACTION_END_OF_DAY_REMINDER = "com.taskplanner.ACTION_END_OF_DAY_REMINDER"

        const val EXTRA_SCHEDULED_HOUR = "EXTRA_SCHEDULED_HOUR"
        const val EXTRA_START_HOUR = "EXTRA_START_HOUR"
        const val EXTRA_END_HOUR = "EXTRA_END_HOUR"
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
        const val EXTRA_EVENT_TITLE = "EXTRA_EVENT_TITLE"
        const val EXTRA_EVENT_TYPE = "EXTRA_EVENT_TYPE"
        const val EXTRA_MEETING_LINK = "EXTRA_MEETING_LINK"
    }
}
