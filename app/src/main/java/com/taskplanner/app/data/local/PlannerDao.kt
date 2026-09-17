package com.taskplanner.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskplanner.app.data.model.SyncedEvent
import com.taskplanner.app.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {

    @Query("SELECT * FROM tasks WHERE scheduledHour = :hour ORDER BY status ASC, priority ASC, id ASC")
    fun getTasksForHourFlow(hour: Int): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE scheduledHour = :hour AND status != 'COMPLETED' AND status != 'CANCELLED'")
    suspend fun getPendingTasksForHour(hour: Int): List<TaskItem>

    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' AND status != 'CANCELLED'")
    suspend fun getAllPendingTasks(): List<TaskItem>

    @Query("SELECT * FROM tasks ORDER BY scheduledHour ASC, priority ASC, id ASC")
    fun getAllTasksFlow(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE status = 'COMPLETED' ORDER BY completedAt DESC, id DESC")
    fun getCompletedTasksFlow(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItem>): List<Long>

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("SELECT * FROM synced_events WHERE startTimeMillis >= :fromTime ORDER BY startTimeMillis ASC")
    fun getUpcomingEventsFlow(fromTime: Long): Flow<List<SyncedEvent>>

    @Query("SELECT * FROM synced_events WHERE startTimeMillis > :now AND reminderScheduled = 0")
    suspend fun getUnscheduledUpcomingEvents(now: Long): List<SyncedEvent>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: SyncedEvent): Long

    @Query("UPDATE synced_events SET reminderScheduled = 1 WHERE eventId = :id")
    suspend fun markReminderScheduled(id: String)
}
