package com.taskplanner.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.taskplanner.app.data.model.SyncedEvent
import com.taskplanner.app.receiver.AlarmReceiver
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun canScheduleExact(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * Schedules the next exact hourly reminder strictly between 6:00 AM and 11:00 PM.
     */
    fun scheduleNextHourlyReminder() {
        if (!canScheduleExact()) return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, 1) // Target top of next hour
        }

        var hour = calendar.get(Calendar.HOUR_OF_DAY)

        // Boundary constraint: 6 AM to 11 PM (hour 6 to 23)
        if (hour < 6) {
            calendar.set(Calendar.HOUR_OF_DAY, 6)
        } else if (hour > 23) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 6)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_HOURLY_REMINDER
            putExtra(AlarmReceiver.EXTRA_SCHEDULED_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            HOURLY_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    /**
     * Schedules an exact wake alarm 30 minutes prior to a detected event.
     */
    fun scheduleEventReminder(event: SyncedEvent) {
        if (!canScheduleExact()) return

        val triggerTime = event.startTimeMillis - (30 * 60 * 1000L)
        if (triggerTime <= System.currentTimeMillis()) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_EVENT_REMINDER
            putExtra(AlarmReceiver.EXTRA_EVENT_ID, event.eventId)
            putExtra(AlarmReceiver.EXTRA_EVENT_TITLE, event.title)
            putExtra(AlarmReceiver.EXTRA_EVENT_TYPE, event.eventType.name)
            putExtra(AlarmReceiver.EXTRA_MEETING_LINK, event.meetingLink)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.eventId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    companion object {
        private const val HOURLY_ALARM_REQUEST_CODE = 9001
    }
}
