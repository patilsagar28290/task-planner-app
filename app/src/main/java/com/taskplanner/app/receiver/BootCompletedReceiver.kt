package com.taskplanner.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.taskplanner.app.alarm.AlarmScheduler
import com.taskplanner.app.data.local.AppDatabase
import com.taskplanner.app.worker.GmailSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = AlarmScheduler(context)
            scheduler.scheduleNextHourlyReminder()

            // Restore all future event alarms from local DB
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(context)
                    val unscheduled = db.plannerDao().getUnscheduledUpcomingEvents(System.currentTimeMillis())
                    for (event in unscheduled) {
                        scheduler.scheduleEventReminder(event)
                        db.plannerDao().markReminderScheduled(event.eventId)
                    }

                    // Enqueue fresh sync
                    val syncRequest = OneTimeWorkRequestBuilder<GmailSyncWorker>().build()
                    WorkManager.getInstance(context).enqueue(syncRequest)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
