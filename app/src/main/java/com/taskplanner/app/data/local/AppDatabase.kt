package com.taskplanner.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.taskplanner.app.data.model.EventType
import com.taskplanner.app.data.model.SyncedEvent
import com.taskplanner.app.data.model.TaskItem
import com.taskplanner.app.data.model.TaskPriority
import com.taskplanner.app.data.model.NotificationFrequency
import com.taskplanner.app.data.model.TaskSource
import com.taskplanner.app.data.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromNotificationFrequency(value: NotificationFrequency): String = value.name

    @TypeConverter
    fun toNotificationFrequency(value: String): NotificationFrequency = try {
        NotificationFrequency.valueOf(value)
    } catch(e: Exception) {
        NotificationFrequency.ONCE
    }
    @TypeConverter
    fun fromEventType(value: EventType): String = value.name

    @TypeConverter
    fun toEventType(value: String): EventType = EventType.valueOf(value)

    @TypeConverter
    fun fromTaskPriority(value: TaskPriority): String = value.name

    @TypeConverter
    fun toTaskPriority(value: String): TaskPriority = try {
        TaskPriority.valueOf(value)
    } catch (e: Exception) {
        TaskPriority.MEDIUM
    }

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String = value.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = try {
        TaskStatus.valueOf(value)
    } catch (e: Exception) {
        TaskStatus.OPEN
    }

    @TypeConverter
    fun fromTaskSource(value: TaskSource): String = value.name

    @TypeConverter
    fun toTaskSource(value: String): TaskSource = try {
        TaskSource.valueOf(value)
    } catch (e: Exception) {
        TaskSource.TEXT
    }
}

@Database(entities = [TaskItem::class, SyncedEvent::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun plannerDao(): PlannerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chrono_do_planner.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
