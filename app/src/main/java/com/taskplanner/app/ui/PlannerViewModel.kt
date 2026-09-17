package com.taskplanner.app.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taskplanner.app.alarm.AlarmScheduler
import com.taskplanner.app.data.local.AppDatabase
import com.taskplanner.app.data.model.SyncedEvent
import com.taskplanner.app.data.model.TaskItem
import com.taskplanner.app.data.model.TaskPriority
import com.taskplanner.app.data.model.TaskSource
import com.taskplanner.app.data.model.TaskStatus
import com.taskplanner.app.util.NaturalLanguageParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).plannerDao()
    private val alarmScheduler = AlarmScheduler(application)
    private val prefs = application.getSharedPreferences("chrono_do_prefs", Context.MODE_PRIVATE)

    val currentHour: Int
        get() = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    private val _activeStartHour = MutableStateFlow(prefs.getInt("start_hour", 8))
    val activeStartHour: StateFlow<Int> = _activeStartHour.asStateFlow()

    private val _activeEndHour = MutableStateFlow(prefs.getInt("end_hour", 20))
    val activeEndHour: StateFlow<Int> = _activeEndHour.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _lastCompletedTask = MutableStateFlow<TaskItem?>(null)
    val lastCompletedTask: StateFlow<TaskItem?> = _lastCompletedTask.asStateFlow()

    val allTasks = dao.getAllTasksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTasks = dao.getCompletedTasksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingEvents = dao.getUpcomingEventsFlow(System.currentTimeMillis())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedHourFilter = MutableStateFlow<Int?>(null)
    val selectedHourFilter: StateFlow<Int?> = _selectedHourFilter.asStateFlow()

    private val _selectedPriorityFilter = MutableStateFlow<TaskPriority?>(null)
    val selectedPriorityFilter: StateFlow<TaskPriority?> = _selectedPriorityFilter.asStateFlow()

    fun setSelectedHourFilter(hour: Int?) {
        _selectedHourFilter.value = hour
    }

    fun setSelectedPriorityFilter(priority: TaskPriority?) {
        _selectedPriorityFilter.value = priority
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    /**
     * Parses and ingests natural language text or voice input into discrete action items.
     */
    fun parseAndAddTask(rawInput: String, defaultHour: Int = currentHour, source: TaskSource = TaskSource.TEXT) {
        if (rawInput.isBlank()) return
        viewModelScope.launch {
            val parsedList = NaturalLanguageParser.parseInput(rawInput, defaultHour)
            for (parsed in parsedList) {
                val newTask = TaskItem(
                    title = parsed.title,
                    scheduledHour = parsed.scheduledHour,
                    priority = parsed.priority,
                    status = TaskStatus.OPEN,
                    isCompleted = false,
                    source = source,
                    notificationFrequency = parsed.notificationFrequency,
                    createdAt = System.currentTimeMillis(),
                    endDateMillis = parsed.endDateMillis
                )
                dao.insertTask(newTask)
            }
        }
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            val isNowCompleted = task.status != TaskStatus.COMPLETED
            val newStatus = if (isNowCompleted) TaskStatus.COMPLETED else TaskStatus.OPEN
            val updatedTask = task.copy(
                status = newStatus,
                isCompleted = isNowCompleted,
                completedAt = if (isNowCompleted) System.currentTimeMillis() else null
            )
            dao.updateTask(updatedTask)

            if (isNowCompleted) {
                _lastCompletedTask.value = updatedTask
            }
        }
    }

    fun snoozeTaskOneHour(task: TaskItem) {
        viewModelScope.launch {
            val nextHour = (task.scheduledHour + 1) % 24
            val updated = task.copy(scheduledHour = nextHour)
            dao.updateTask(updated)
        }
    }

    fun updateTask(task: TaskItem) {
        viewModelScope.launch {
            dao.updateTask(task)
        }
    }

    fun updateTaskStatus(task: TaskItem, newStatus: TaskStatus) {
        viewModelScope.launch {
            val isCompleted = newStatus == TaskStatus.COMPLETED
            val updatedTask = task.copy(
                status = newStatus,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) System.currentTimeMillis() else null
            )
            dao.updateTask(updatedTask)

            if (isCompleted) {
                _lastCompletedTask.value = updatedTask
            }
        }
    }

    fun undoLastCompletion() {
        val taskToUndo = _lastCompletedTask.value ?: return
        viewModelScope.launch {
            val restored = taskToUndo.copy(
                status = TaskStatus.OPEN,
                isCompleted = false,
                completedAt = null
            )
            dao.updateTask(restored)
            _lastCompletedTask.value = null
        }
    }

    fun clearUndo() {
        _lastCompletedTask.value = null
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }

    fun updateActiveWindow(start: Int, end: Int) {
        _activeStartHour.value = start
        _activeEndHour.value = end
        prefs.edit()
            .putInt("start_hour", start)
            .putInt("end_hour", end)
            .apply()

        // Reschedule alarms with new boundaries
        alarmScheduler.scheduleNextHourlyReminder(start, end)
        alarmScheduler.scheduleEndOfDayReminder(end)
    }

    fun triggerTestNotification() {
        viewModelScope.launch {
            val pending = dao.getAllPendingTasks()
            val helper = com.taskplanner.app.notification.NotificationHelper(getApplication())
            helper.showHourlyNotification(currentHour, pending)
        }
    }
}
