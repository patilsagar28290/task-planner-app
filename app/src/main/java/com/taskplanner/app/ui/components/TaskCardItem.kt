package com.taskplanner.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskplanner.app.data.model.TaskItem
import com.taskplanner.app.data.model.NotificationFrequency
import com.taskplanner.app.data.model.TaskPriority
import com.taskplanner.app.data.model.TaskSource
import com.taskplanner.app.data.model.TaskStatus
import com.taskplanner.app.ui.theme.AlertAmber
import com.taskplanner.app.ui.theme.CardSlate
import com.taskplanner.app.ui.theme.CardSlateBorder
import com.taskplanner.app.ui.theme.DeepSlate
import com.taskplanner.app.ui.theme.ElectricMint
import com.taskplanner.app.ui.theme.PriorityHighRed
import com.taskplanner.app.ui.theme.SlateTextPrimary
import com.taskplanner.app.ui.theme.SlateTextSecondary
import com.taskplanner.app.ui.theme.SurfaceElevated
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TaskCardItem(
    task: TaskItem,
    onToggleComplete: () -> Unit,
    onSnoozeOneHour: () -> Unit,
    onUpdateTask: (TaskItem) -> Unit,
    onStatusChanged: (TaskStatus) -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val isCompleted = task.status == TaskStatus.COMPLETED
    val isCancelled = task.status == TaskStatus.CANCELLED

    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "checkScale"
    )

    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> PriorityHighRed
        TaskPriority.MEDIUM -> AlertAmber
        TaskPriority.LOW -> ElectricMint
    }

    val cardBgColor by animateColorAsState(
        targetValue = when {
            isCompleted -> SurfaceElevated.copy(alpha = 0.5f)
            else -> SurfaceElevated
        },
        label = "bgColor"
    )

    val accentBorderColor by animateColorAsState(
        targetValue = when {
            isCompleted -> ElectricMint.copy(alpha = 0.3f)
            task.priority == TaskPriority.HIGH -> PriorityHighRed.copy(alpha = 0.5f)
            else -> Color.Transparent
        },
        label = "borderColor"
    )

    val dateFormat = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }

    if (showEditDialog) {
        TaskEditDialog(
            task = task,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedTask ->
                onUpdateTask(updatedTask)
                showEditDialog = false
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // Thin priority accent bar at top
        if (!isCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(priorityColor.copy(alpha = 0.7f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row 1: Checkbox + Title + Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .scale(checkScale)
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isCompleted) ElectricMint else Color.Transparent)
                        .border(
                            width = 1.5.dp,
                            color = if (isCompleted) ElectricMint else CardSlateBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleComplete()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Color.Black,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Title
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            lineHeight = 22.sp
                        ),
                        color = if (isCompleted) SlateTextSecondary else SlateTextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Edit button
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = SlateTextSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Metadata chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Scheduled hour
                MetadataChip(
                    icon = { Icon(Icons.Default.Schedule, null, tint = SlateTextSecondary, modifier = Modifier.size(12.dp)) },
                    label = String.format("%02d:00", task.scheduledHour)
                )

                // Priority
                MetadataChip(
                    backgroundColor = priorityColor.copy(alpha = 0.12f),
                    label = task.priority.name.lowercase()
                        .replaceFirstChar { it.titlecase() },
                    labelColor = priorityColor
                )

                // End date
                task.endDateMillis?.let { endMillis ->
                    MetadataChip(
                        icon = { Icon(Icons.Default.CalendarToday, null, tint = SlateTextSecondary, modifier = Modifier.size(11.dp)) },
                        label = dateFormat.format(Date(endMillis))
                    )
                }

                // Notification Freq
                if (task.notificationFrequency != NotificationFrequency.ONCE) {
                    MetadataChip(
                        icon = { Icon(Icons.Default.AccessTime, null, tint = SlateTextSecondary, modifier = Modifier.size(11.dp)) },
                        label = task.notificationFrequency.name.lowercase().replaceFirstChar { it.titlecase() }
                    )
                }

                // Voice indicator
                if (task.source == TaskSource.VOICE) {
                    MetadataChip(label = "🎤 Voice", labelColor = ElectricMint)
                }
            }

            // Row 3: Action buttons - only for non-completed tasks
            if (!isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = CardSlateBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mark Done
                    ActionTextButton(
                        label = "Done",
                        icon = Icons.Default.Check,
                        color = ElectricMint,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleComplete()
                        }
                    )

                    // Snooze
                    ActionTextButton(
                        label = "Snooze 1h",
                        icon = Icons.Default.Snooze,
                        color = AlertAmber,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSnoozeOneHour()
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Remove
                    ActionTextButton(
                        label = "Remove",
                        icon = Icons.Default.Close,
                        color = PriorityHighRed.copy(alpha = 0.7f),
                        onClick = { onDeleteTask() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MetadataChip(
    label: String,
    labelColor: Color = SlateTextSecondary,
    backgroundColor: Color = CardSlate.copy(alpha = 0.5f),
    icon: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.invoke()
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = labelColor
            )
        }
    }
}

@Composable
private fun ActionTextButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = color
        )
    }
}

@Composable
fun TaskEditDialog(
    task: TaskItem,
    onDismiss: () -> Unit,
    onConfirm: (TaskItem) -> Unit
) {
    var editedTitle by remember { mutableStateOf(task.title) }
    var editedHour by remember { mutableStateOf(task.scheduledHour.toString()) }
    var editedPriority by remember { mutableStateOf(task.priority) }
    var editedFrequency by remember { mutableStateOf(task.notificationFrequency) }
    var editedEndDateMillis by remember { mutableStateOf<Long?>(task.endDateMillis) }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepSlate,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Edit Action Item",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SlateTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("Title", color = SlateTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricMint,
                        unfocusedBorderColor = CardSlateBorder,
                        focusedTextColor = SlateTextPrimary,
                        unfocusedTextColor = SlateTextPrimary,
                        cursorColor = ElectricMint,
                        focusedLabelColor = ElectricMint
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = editedHour,
                    onValueChange = { editedHour = it },
                    label = { Text("Hour (0–23)", color = SlateTextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricMint,
                        unfocusedBorderColor = CardSlateBorder,
                        focusedTextColor = SlateTextPrimary,
                        unfocusedTextColor = SlateTextPrimary,
                        cursorColor = ElectricMint,
                        focusedLabelColor = ElectricMint
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // End date
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Due date",
                        style = MaterialTheme.typography.labelMedium,
                        color = SlateTextSecondary
                    )
                    Text(
                        text = editedEndDateMillis?.let { dateFormat.format(Date(it)) } ?: "Not set",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = if (editedEndDateMillis != null) ElectricMint else SlateTextSecondary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val today = Calendar.getInstance()
                        listOf(
                            "None" to null,
                            "Today" to Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
                            }.timeInMillis,
                            "Tomorrow" to Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, 1)
                                set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
                            }.timeInMillis,
                            "+7 days" to Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, 7)
                                set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
                            }.timeInMillis
                        ).forEach { (label, millis) ->
                            val isActive = (millis == null && editedEndDateMillis == null) ||
                                    (millis != null && editedEndDateMillis == millis)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isActive) ElectricMint.copy(alpha = 0.15f) else CardSlate.copy(alpha = 0.4f))
                                    .border(
                                        width = if (isActive) 1.dp else 0.dp,
                                        color = if (isActive) ElectricMint.copy(alpha = 0.5f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { editedEndDateMillis = millis }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isActive) ElectricMint else SlateTextSecondary
                                )
                            }
                        }
                    }
                }

                // Priority
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.labelMedium,
                        color = SlateTextSecondary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TaskPriority.values().forEach { priority ->
                            val isSelected = editedPriority == priority
                            val chipColor = when (priority) {
                                TaskPriority.HIGH -> PriorityHighRed
                                TaskPriority.MEDIUM -> AlertAmber
                                TaskPriority.LOW -> ElectricMint
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) chipColor.copy(alpha = 0.2f) else CardSlate.copy(alpha = 0.4f))
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) chipColor else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { editedPriority = priority }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = priority.name.lowercase().replaceFirstChar { it.titlecase() },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isSelected) chipColor else SlateTextSecondary
                                )
                            }
                        }
                    }
                }

                // Frequency
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Reminder Freq.",
                        style = MaterialTheme.typography.labelMedium,
                        color = SlateTextSecondary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        NotificationFrequency.values().forEach { freq ->
                            val isSelected = editedFrequency == freq
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ElectricMint.copy(alpha = 0.15f) else CardSlate.copy(alpha = 0.4f))
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) ElectricMint.copy(alpha = 0.5f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { editedFrequency = freq }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = freq.name.lowercase().replaceFirstChar { it.titlecase() },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) ElectricMint else SlateTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val hourParsed = editedHour.toIntOrNull()?.coerceIn(0, 23) ?: task.scheduledHour
                    onConfirm(
                        task.copy(
                            title = editedTitle.ifBlank { task.title },
                            scheduledHour = hourParsed,
                            priority = editedPriority,
                            notificationFrequency = editedFrequency,
                            endDateMillis = editedEndDateMillis
                        )
                    )
                }
            ) {
                Text("Save", color = ElectricMint, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateTextSecondary)
            }
        }
    )
}
