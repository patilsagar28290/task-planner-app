package com.taskplanner.app.ui.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskplanner.app.data.model.NotificationFrequency
import com.taskplanner.app.data.model.TaskPriority
import com.taskplanner.app.data.model.TaskSource
import com.taskplanner.app.ui.theme.AlertAmber
import com.taskplanner.app.ui.theme.CardSlate
import com.taskplanner.app.ui.theme.CardSlateBorder
import com.taskplanner.app.ui.theme.DeepSlate
import com.taskplanner.app.ui.theme.ElectricMint
import com.taskplanner.app.ui.theme.PriorityHighRed
import com.taskplanner.app.ui.theme.SlateTextPrimary
import com.taskplanner.app.ui.theme.SlateTextSecondary
import com.taskplanner.app.ui.theme.SurfaceElevated
import com.taskplanner.app.util.NaturalLanguageParser
import com.taskplanner.app.util.VoiceInputHelper

@Composable
fun QuickCaptureDock(
    onTaskSubmitted: (String, TaskSource) -> Unit,
    currentHour: Int,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Speech Recognizer
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = VoiceInputHelper.parseSpeechResult(result.data)
            if (!spokenText.isNullOrBlank()) {
                onTaskSubmitted(spokenText, TaskSource.VOICE)
            }
        }
    }

    // Live parse preview
    val parsedPreview = remember(inputText) {
        if (inputText.isNotBlank()) {
            NaturalLanguageParser.parseInput(inputText, currentHour)
        } else emptyList()
    }

    if (showAddDialog) {
        NewActionItemDialog(
            currentHour = currentHour,
            onDismiss = { showAddDialog = false },
            onConfirm = { rawText ->
                onTaskSubmitted(rawText, TaskSource.TEXT)
                showAddDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceElevated)
            .border(1.dp, CardSlateBorder.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        // Smart parse preview
        AnimatedVisibility(
            visible = parsedPreview.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detected:",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextSecondary
                )
                parsedPreview.firstOrNull()?.let { preview ->
                    val priorityColor = when (preview.priority) {
                        TaskPriority.HIGH -> PriorityHighRed
                        TaskPriority.MEDIUM -> AlertAmber
                        TaskPriority.LOW -> ElectricMint
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(priorityColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = preview.priority.name.lowercase().replaceFirstChar { it.titlecase() },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                            color = priorityColor
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CardSlate.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = String.format("%02d:00", preview.scheduledHour),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                            color = SlateTextPrimary
                        )
                    }
                }
            }
        }

        // Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Add button
            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ElectricMint.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New action item",
                    tint = ElectricMint,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Text Input
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Quick capture…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DeepSlate,
                    unfocusedContainerColor = DeepSlate,
                    focusedBorderColor = ElectricMint.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = SlateTextPrimary,
                    unfocusedTextColor = SlateTextPrimary,
                    cursorColor = ElectricMint
                ),
                shape = RoundedCornerShape(14.dp),
                maxLines = 2,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (inputText.isNotBlank()) {
                        onTaskSubmitted(inputText, TaskSource.TEXT)
                        inputText = ""
                    }
                })
            )

            // Mic button
            val pulseScale by rememberInfiniteTransition(label = "micPulse").animateFloat(
                initialValue = 1f,
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            IconButton(
                onClick = {
                    isListening = true
                    speechLauncher.launch(VoiceInputHelper.createSpeechIntent())
                },
                modifier = Modifier
                    .size(40.dp)
                    .scale(if (isListening) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(if (isListening) AlertAmber.copy(alpha = 0.2f) else Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice capture",
                    tint = if (isListening) AlertAmber else SlateTextSecondary
                )
            }

            // Send button
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onTaskSubmitted(inputText, TaskSource.TEXT)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank()) ElectricMint else Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Submit",
                    tint = if (inputText.isNotBlank()) Color.Black else SlateTextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun NewActionItemDialog(
    currentHour: Int,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(currentHour.toString()) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var selectedFrequency by remember { mutableStateOf(NotificationFrequency.ONCE) }
    var selectedDateTag by remember { mutableStateOf("Today") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepSlate,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "New Action Item",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SlateTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What needs to be done?", color = SlateTextSecondary) },
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
                    value = hour,
                    onValueChange = { hour = it },
                    label = { Text("Scheduled hour (0–23)", color = SlateTextSecondary) },
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

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Due date", style = MaterialTheme.typography.labelMedium, color = SlateTextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Today", "Tomorrow", "Next Week").forEach { dateLabel ->
                            val isActive = selectedDateTag == dateLabel
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isActive) ElectricMint.copy(alpha = 0.15f) else CardSlate.copy(alpha = 0.4f))
                                    .border(
                                        width = if (isActive) 1.dp else 0.dp,
                                        color = if (isActive) ElectricMint.copy(alpha = 0.5f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDateTag = dateLabel }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = dateLabel,
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
                    Text("Priority", style = MaterialTheme.typography.labelMedium, color = SlateTextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TaskPriority.values().forEach { priority ->
                            val isSelected = selectedPriority == priority
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
                                    .clickable { selectedPriority = priority }
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
                    Text("Reminder Freq.", style = MaterialTheme.typography.labelMedium, color = SlateTextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        NotificationFrequency.values().forEach { freq ->
                            val isSelected = selectedFrequency == freq
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ElectricMint.copy(alpha = 0.15f) else CardSlate.copy(alpha = 0.4f))
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) ElectricMint.copy(alpha = 0.5f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedFrequency = freq }
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
                    if (title.isNotBlank()) {
                        val parsedHour = hour.toIntOrNull()?.coerceIn(0, 23) ?: currentHour
                        // encode frequency in command string or just add it to NLP string for now (since we use NaturalLanguageParser)
                        val formattedCommand = "$title by $parsedHour:00 - ${selectedPriority.name} - due $selectedDateTag - freq ${selectedFrequency.name}"
                        onConfirm(formattedCommand)
                    }
                }
            ) {
                Text("Create", color = ElectricMint, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateTextSecondary)
            }
        }
    )
}
