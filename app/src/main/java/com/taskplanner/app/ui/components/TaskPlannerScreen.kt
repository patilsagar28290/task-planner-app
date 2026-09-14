package com.taskplanner.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taskplanner.app.data.model.SyncedEvent
import com.taskplanner.app.data.model.TaskItem
import com.taskplanner.app.ui.PlannerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPlannerScreen(viewModel: PlannerViewModel) {
    val tasks by viewModel.allTasks.collectAsState()
    val events by viewModel.upcomingEvents.collectAsState()
    var newTaskTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Planner & Reminders") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Hourly Planner (6:00 AM - 11:00 PM)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Add task for current hour") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addTask(newTaskTitle, viewModel.currentHour)
                            newTaskTitle = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }
            }

            if (events.isNotEmpty()) {
                item {
                    Text(
                        text = "Synced Workshops, Bootcamps & Webinars (30m Alert Active)",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(events) { event ->
                    EventItemCard(event)
                }
            }

            item {
                Text(
                    text = "Scheduled Tasks",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(tasks) { task ->
                TaskItemRow(task = task, onToggle = { viewModel.toggleTask(task) })
            }
        }
    }
}

@Composable
fun EventItemCard(event: SyncedEvent) {
    val timeFormat = SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "${event.eventType.name} • ${timeFormat.format(Date(event.startTimeMillis))}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TaskItemRow(task: TaskItem, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = "Toggle completion"
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Scheduled for ${task.scheduledHour}:00",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
