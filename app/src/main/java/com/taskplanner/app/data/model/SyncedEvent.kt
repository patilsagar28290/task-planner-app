package com.taskplanner.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EventType {
    WORKSHOP,
    BOOTCAMP,
    WEBINAR
}

@Entity(tableName = "synced_events")
data class SyncedEvent(
    @PrimaryKey
    val eventId: String, // Unique identifier (e.g. Gmail Message ID)
    val title: String,
    val eventType: EventType,
    val startTimeMillis: Long,
    val endTimeMillis: Long? = null,
    val meetingLink: String? = null,
    val reminderScheduled: Boolean = false
)
