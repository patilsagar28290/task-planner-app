package com.taskplanner.app

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.taskplanner.app.alarm.AlarmScheduler
import com.taskplanner.app.notification.NotificationHelper
import com.taskplanner.app.worker.GmailSyncWorker
import java.util.concurrent.TimeUnit

class TaskPlannerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize notification channels
        NotificationHelper(this).createChannels()

        // 2. Schedule hourly alarms (6am - 11pm)
        val alarmScheduler = AlarmScheduler(this)
        alarmScheduler.scheduleNextHourlyReminder()

        // 3. Register periodic Gmail sync worker
        setupPeriodicGmailSync()
    }

    private fun setupPeriodicGmailSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWork = PeriodicWorkRequestBuilder<GmailSyncWorker>(2, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "GmailSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWork
        )
    }
}
