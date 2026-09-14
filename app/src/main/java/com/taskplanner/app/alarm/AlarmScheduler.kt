package com.taskplanner.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.taskplanner.app.receiver.AlarmReceiver
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNextHourlyReminder() {
        val calendar = Calendar.getInstance()
        
        // Start from 6 AM
        if (calendar.get(Calendar.HOUR_OF_DAY) < 6) {
            calendar.set(Calendar.HOUR_OF_DAY, 6)
        } else if (calendar.get(Calendar.HOUR_OF_DAY) >= 23) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 6)
        } else {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }
        
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.taskplanner.ACTION_HOURLY_REMINDER"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            HOURLY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val HOURLY_REMINDER_REQUEST_CODE = 1001
    }
}
