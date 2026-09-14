package com.taskplanner.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.taskplanner.app.alarm.AlarmScheduler
import com.taskplanner.app.notification.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        Log.d(TAG, "Alarm received: ${intent.action}")

        when (intent.action) {
            ACTION_HOURLY_REMINDER -> {
                handleHourlyReminder(context)
                scheduleNextReminder(context)
            }
            ACTION_EVENT_REMINDER -> {
                handleEventReminder(context)
            }
        }
    }

    private fun handleHourlyReminder(context: Context) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showHourlyReminderNotification()
    }

    private fun handleEventReminder(context: Context) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showEventReminderNotification()
    }

    private fun scheduleNextReminder(context: Context) {
        val alarmScheduler = AlarmScheduler(context)
        alarmScheduler.scheduleNextHourlyReminder()
    }

    companion object {
        private const val TAG = "AlarmReceiver"
        private const val ACTION_HOURLY_REMINDER = "com.taskplanner.ACTION_HOURLY_REMINDER"
        private const val ACTION_EVENT_REMINDER = "com.taskplanner.ACTION_EVENT_REMINDER"
    }
}
