package com.taskplanner.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskplanner.app.data.model.SyncedEvent
import com.taskplanner.app.data.model.TaskItem
import com.taskplanner.app.data.model.TaskPriority
import com.taskplanner.app.data.model.TaskStatus
import com.taskplanner.app.ui.PlannerViewModel
import com.taskplanner.app.ui.theme.AlertAmber
import com.taskplanner.app.ui.theme.CardSlate
import com.taskplanner.app.ui.theme.CardSlateBorder
import com.taskplanner.app.ui.theme.DarkSlateBackground
import com.taskplanner.app.ui.theme.DeepSlate
import com.taskplanner.app.ui.theme.ElectricMint
import com.taskplanner.app.ui.theme.PriorityHighRed
import com.taskplanner.app.ui.theme.SlateTextPrimary
import com.taskplanner.app.ui.theme.SlateTextSecondary
import com.taskplanner.app.ui.theme.SurfaceElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TaskPlannerScreen(viewModel: PlannerViewModel) {
    val tasks by viewModel.allTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val events by viewModel.upcomingEvents.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val startHour by viewModel.activeStartHour.collectAsState()
    val endHour by viewModel.activeEndHour.collectAsState()
    val lastCompletedTask by viewModel.lastCompletedTask.collectAsState()
    val selectedHourFilter by viewModel.selectedHourFilter.collectAsState()
    val selectedPriorityFilter by viewModel.selectedPriorityFilter.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(lastCompletedTask) {
        lastCompletedTask?.let { task ->
            val result = snackbarHostState.showSnackbar(
                message = "✓ '${task.title}' completed",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoLastCompletion()
            } else {
                viewModel.clearUndo()
            }
        }
    }

    val pendingTasks = remember(tasks) {
        tasks.filter { it.status != TaskStatus.COMPLETED && it.status != TaskStatus.CANCELLED }
    }

    Scaffold(
        topBar = {
            ChronoDoHeader(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.setSelectedTab(it) },
                activePendingCount = pendingTasks.size,
                startHour = startHour,
                endHour = endHour
            )
        },
        bottomBar = {
            QuickCaptureDock(
                onTaskSubmitted = { rawText, source ->
                    viewModel.parseAndAddTask(rawText, viewModel.currentHour, source)
                },
                currentHour = viewModel.currentHour,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkSlateBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(animationSpec = androidx.compose.animation.core.tween(250)) with fadeOut(animationSpec = androidx.compose.animation.core.tween(250)) },
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    0 -> HourlyLoopDashboard(
                        pendingTasks = pendingTasks,
                        upcomingEvents = events,
                        currentHour = viewModel.currentHour,
                        selectedHourFilter = selectedHourFilter,
                        selectedPriorityFilter = selectedPriorityFilter,
                        onSelectHourFilter = { viewModel.setSelectedHourFilter(it) },
                        onSelectPriorityFilter = { viewModel.setSelectedPriorityFilter(it) },
                        onToggleComplete = { viewModel.toggleTask(it) },
                        onSnoozeOneHour = { viewModel.snoozeTaskOneHour(it) },
                        onUpdateTask = { viewModel.updateTask(it) },
                        onStatusChanged = { task, newStatus -> viewModel.updateTaskStatus(task, newStatus) },
                        onDeleteTask = { viewModel.deleteTask(it) }
                    )

                    1 -> DailyLogView(
                        totalTasksCount = tasks.size,
                        completedTasks = completedTasks,
                        onRestoreTask = { viewModel.toggleTask(it) }
                    )

                    2 -> ActiveWindowSettingsView(
                        startHour = startHour,
                        endHour = endHour,
                        onActiveWindowChanged = { s, e -> viewModel.updateActiveWindow(s, e) },
                        onTriggerTestNotification = { viewModel.triggerTestNotification() }
                    )
                }
            }
        }
    }
}

@Composable
fun HourlyLoopDashboard(
    pendingTasks: List<TaskItem>,
    upcomingEvents: List<SyncedEvent>,
    currentHour: Int,
    selectedHourFilter: Int?,
    selectedPriorityFilter: TaskPriority?,
    onSelectHourFilter: (Int?) -> Unit,
    onSelectPriorityFilter: (TaskPriority?) -> Unit,
    onToggleComplete: (TaskItem) -> Unit,
    onSnoozeOneHour: (TaskItem) -> Unit,
    onUpdateTask: (TaskItem) -> Unit,
    onStatusChanged: (TaskItem, TaskStatus) -> Unit,
    onDeleteTask: (TaskItem) -> Unit
) {
    val filteredTasks = remember(pendingTasks, selectedHourFilter, selectedPriorityFilter) {
        pendingTasks.filter { task ->
            (selectedHourFilter == null || task.scheduledHour == selectedHourFilter) &&
            (selectedPriorityFilter == null || task.priority == selectedPriorityFilter)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Current hour focus
        item {
            val currentHourTasks = pendingTasks.filter { it.scheduledHour == currentHour }
            CurrentHourCard(currentHour = currentHour, taskCount = currentHourTasks.size, topTask = currentHourTasks.firstOrNull())
        }

        // Hour filter strip
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Timeline",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = SlateTextPrimary
                    )
                    if (selectedHourFilter != null || selectedPriorityFilter != null) {
                        Text(
                            text = "Clear filters",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = ElectricMint,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    onSelectHourFilter(null)
                                    onSelectPriorityFilter(null)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Hour pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterPill(
                            label = "All",
                            count = pendingTasks.size,
                            isSelected = selectedHourFilter == null,
                            onClick = { onSelectHourFilter(null) }
                        )
                    }
                    items((0..23).toList()) { hour ->
                        val count = pendingTasks.count { it.scheduledHour == hour }
                        val isCurrent = hour == currentHour
                        FilterPill(
                            label = String.format("%02d:00", hour),
                            count = count,
                            isSelected = selectedHourFilter == hour,
                            isCurrent = isCurrent,
                            onClick = { onSelectHourFilter(if (selectedHourFilter == hour) null else hour) }
                        )
                    }
                }

                // Priority pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TaskPriority.values().forEach { priority ->
                        val isActive = selectedPriorityFilter == priority
                        val color = when (priority) {
                            TaskPriority.HIGH -> PriorityHighRed
                            TaskPriority.MEDIUM -> AlertAmber
                            TaskPriority.LOW -> ElectricMint
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isActive) color.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = if (isActive) color.copy(alpha = 0.5f) else CardSlateBorder.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectPriorityFilter(if (isActive) null else priority) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = priority.name.lowercase().replaceFirstChar { it.titlecase() },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isActive) color else SlateTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Events
        if (upcomingEvents.isNotEmpty()) {
            item {
                Text(
                    text = "Upcoming Events",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = SlateTextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            items(upcomingEvents) { event ->
                SyncedEventCard(event)
            }
        }

        // Task list header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Action Items",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = SlateTextPrimary
                )
                Text(
                    text = "${filteredTasks.size} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextSecondary
                )
            }
        }

        if (filteredTasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = CardSlateBorder.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (pendingTasks.isEmpty()) "No pending items" else "No items match filters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
                TaskCardItem(
                    task = task,
                    onToggleComplete = { onToggleComplete(task) },
                    onSnoozeOneHour = { onSnoozeOneHour(task) },
                    onUpdateTask = { onUpdateTask(it) },
                    onStatusChanged = { newStatus -> onStatusChanged(task, newStatus) },
                    onDeleteTask = { onDeleteTask(task) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun CurrentHourCard(currentHour: Int, taskCount: Int, topTask: TaskItem?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AlertAmber.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Now · ${String.format("%02d:00", currentHour)}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = AlertAmber
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (topTask != null) topTask.title else "No items this hour",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (topTask != null) SlateTextPrimary else SlateTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AlertAmber.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$taskCount",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = AlertAmber
                )
            }
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    count: Int = 0,
    isSelected: Boolean,
    isCurrent: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = when {
        isSelected -> ElectricMint.copy(alpha = 0.15f)
        isCurrent -> AlertAmber.copy(alpha = 0.08f)
        else -> Color.Transparent
    }
    val borderColor = when {
        isSelected -> ElectricMint.copy(alpha = 0.5f)
        isCurrent -> AlertAmber.copy(alpha = 0.3f)
        else -> CardSlateBorder.copy(alpha = 0.25f)
    }
    val textColor = when {
        isSelected -> ElectricMint
        isCurrent -> AlertAmber
        else -> SlateTextSecondary
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected || isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 11.sp
                ),
                color = textColor
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ElectricMint.copy(alpha = 0.3f) else CardSlate.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = if (isSelected) ElectricMint else SlateTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SyncedEventCard(event: SyncedEvent) {
    val timeFormat = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElectricMint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = ElectricMint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = SlateTextPrimary
                )
                Text(
                    text = timeFormat.format(Date(event.startTimeMillis)),
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextSecondary
                )
            }
        }
    }
}
