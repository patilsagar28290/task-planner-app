package com.taskplanner.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taskplanner.app.alarm.AlarmScheduler
import com.taskplanner.app.data.local.AppDatabase
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
                
                // Fetch pending count asynchronously before notifying
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val pendingCount = if (hour in 6..23) {
                            AppDatabase.getInstance(context).plannerDao().getPendingTasksForHour(hour).size
                        } else 0
                        notificationHelper.showHourlyNotification(hour, pendingCount)
                    } finally {
                        pendingResult.finish()
                    }
                }

                // Chain the next hourly alarm
                scheduler.scheduleNextHourlyReminder()
            }

            ACTION_EVENT_REMINDER -> {
                val title = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Upcoming Event"
                val type = intent.getStringExtra(EXTRA_EVENT_TYPE) ?: "Workshop/Webinar"
                val link = intent.getStringExtra(EXTRA_MEETING_LINK)
                notificationHelper.showEventNotification(title, type, link)
            }
        }
    }

    companion object {
        const val ACTION_HOURLY_REMINDER = "com.taskplanner.ACTION_HOURLY_REMINDER"
        const val ACTION_EVENT_REMINDER = "com.taskplanner.ACTION_EVENT_REMINDER"
        const val EXTRA_SCHEDULED_HOUR = "EXTRA_SCHEDULED_HOUR"
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
        const val EXTRA_EVENT_TITLE = "EXTRA_EVENT_TITLE"
        const val EXTRA_EVENT_TYPE = "EXTRA_EVENT_TYPE"
        const val EXTRA_MEETING_LINK = "EXTRA_MEETING_LINK"
    }
}
