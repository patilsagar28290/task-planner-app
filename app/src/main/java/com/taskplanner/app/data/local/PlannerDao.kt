package com.taskplanner.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskplanner.app.data.model.SyncedEvent
import com.taskplanner.app.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {

    @Query("SELECT * FROM tasks WHERE scheduledHour = :hour ORDER BY isCompleted ASC, id ASC")
    fun getTasksForHourFlow(hour: Int): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE scheduledHour = :hour AND isCompleted = 0")
    suspend fun getPendingTasksForHour(hour: Int): List<TaskItem>

    @Query("SELECT * FROM tasks ORDER BY scheduledHour ASC, id ASC")
    fun getAllTasksFlow(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Query("SELECT * FROM synced_events WHERE startTimeMillis >= :fromTime ORDER BY startTimeMillis ASC")
    fun getUpcomingEventsFlow(fromTime: Long): Flow<List<SyncedEvent>>

    @Query("SELECT * FROM synced_events WHERE startTimeMillis > :now AND reminderScheduled = 0")
    suspend fun getUnscheduledUpcomingEvents(now: Long): List<SyncedEvent>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: SyncedEvent): Long

    @Query("UPDATE synced_events SET reminderScheduled = 1 WHERE eventId = :id")
    suspend fun markReminderScheduled(id: String)
}
