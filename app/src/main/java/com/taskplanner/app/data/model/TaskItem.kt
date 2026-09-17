package com.taskplanner.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority {
    HIGH, MEDIUM, LOW
}

enum class TaskStatus {
    OPEN, IN_PROGRESS, COMPLETED, CANCELLED
}

enum class TaskSource {
    TEXT, VOICE
}

enum class NotificationFrequency {
    NONE, ONCE, HOURLY, DAILY
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val scheduledHour: Int, // 0 to 23
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.OPEN,
    val isCompleted: Boolean = false,
    val source: TaskSource = TaskSource.TEXT,
    val notificationFrequency: NotificationFrequency = NotificationFrequency.ONCE,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val endDateMillis: Long? = null
)
