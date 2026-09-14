package com.taskplanner.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taskplanner.app.data.local.AppDatabase
import com.taskplanner.app.data.model.TaskItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).plannerDao()

    val currentHour: Int
        get() = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val allTasks = dao.getAllTasksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingEvents = dao.getUpcomingEventsFlow(System.currentTimeMillis())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(title: String, hour: Int) {
        viewModelScope.launch {
            dao.insertTask(TaskItem(title = title, scheduledHour = hour))
        }
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            dao.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }
}
