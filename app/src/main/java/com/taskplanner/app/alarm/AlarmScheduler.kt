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
     * Schedules the next exact hourly reminder strictly within the active window (default 8 AM to 8 PM).
     */
    fun scheduleNextHourlyReminder(startHour: Int = 8, endHour: Int = 20) {
        if (!canScheduleExact()) return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, 1) // Target top of next hour
        }

        var hour = calendar.get(Calendar.HOUR_OF_DAY)

        // Boundary constraint: active window (e.g. 8 to 20)
        if (hour < startHour) {
            calendar.set(Calendar.HOUR_OF_DAY, startHour)
        } else if (hour > endHour) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, startHour)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_HOURLY_REMINDER
            putExtra(AlarmReceiver.EXTRA_SCHEDULED_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
            putExtra(AlarmReceiver.EXTRA_START_HOUR, startHour)
            putExtra(AlarmReceiver.EXTRA_END_HOUR, endHour)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            HOURLY_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Fallback for missing exact alarm permission
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun snoozeHourlyReminder(minutes: Int = 60) {
        val triggerTime = System.currentTimeMillis() + (minutes * 60 * 1000L)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_HOURLY_REMINDER
            putExtra(AlarmReceiver.EXTRA_SCHEDULED_HOUR, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            HOURLY_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: Exception) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
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

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: Exception) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    /**
     * Schedules an end of day reminder for pending tasks.
     */
    fun scheduleEndOfDayReminder(endHour: Int = 20) {
        if (!canScheduleExact()) return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // if time has passed for today, set for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_END_OF_DAY_REMINDER
            putExtra(AlarmReceiver.EXTRA_END_HOUR, endHour)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            END_OF_DAY_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    companion object {
        private const val HOURLY_ALARM_REQUEST_CODE = 9001
        private const val END_OF_DAY_ALARM_REQUEST_CODE = 9002
    }
}
