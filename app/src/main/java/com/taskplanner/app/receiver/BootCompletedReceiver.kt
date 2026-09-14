package com.taskplanner.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.taskplanner.app.alarm.AlarmScheduler

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device boot completed, rescheduling alarms")
            
            // Reschedule alarms after device reboot
            val alarmScheduler = AlarmScheduler(context)
            alarmScheduler.scheduleNextHourlyReminder()
        }
    }

    companion object {
        private const val TAG = "BootCompletedReceiver"
    }
}
