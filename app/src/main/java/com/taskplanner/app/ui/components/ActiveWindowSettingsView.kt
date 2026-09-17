package com.taskplanner.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskplanner.app.ui.theme.AlertAmber
import com.taskplanner.app.ui.theme.CardSlate
import com.taskplanner.app.ui.theme.CardSlateBorder
import com.taskplanner.app.ui.theme.ElectricMint
import com.taskplanner.app.ui.theme.SlateTextPrimary
import com.taskplanner.app.ui.theme.SlateTextSecondary
import com.taskplanner.app.ui.theme.SurfaceElevated

@Composable
fun ActiveWindowSettingsView(
    startHour: Int,
    endHour: Int,
    onActiveWindowChanged: (Int, Int) -> Unit,
    onTriggerTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    var localStart by remember(startHour) { mutableFloatStateOf(startHour.toFloat()) }
    var localEnd by remember(endHour) { mutableFloatStateOf(endHour.toFloat()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Active window card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Active hours",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Hourly nudges run during this window",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Start
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Start",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateTextSecondary
                        )
                        Text(
                            text = String.format("%02d:00", localStart.toInt()),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AlertAmber
                        )
                    }
                    Slider(
                        value = localStart,
                        onValueChange = {
                            localStart = it.coerceAtMost(localEnd - 1)
                            onActiveWindowChanged(localStart.toInt(), localEnd.toInt())
                        },
                        valueRange = 0f..23f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = AlertAmber,
                            activeTrackColor = AlertAmber,
                            inactiveTrackColor = CardSlate
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // End
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "End",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateTextSecondary
                        )
                        Text(
                            text = String.format("%02d:00", localEnd.toInt()),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElectricMint
                        )
                    }
                    Slider(
                        value = localEnd,
                        onValueChange = {
                            localEnd = it.coerceAtLeast(localStart + 1)
                            onActiveWindowChanged(localStart.toInt(), localEnd.toInt())
                        },
                        valueRange = 0f..23f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricMint,
                            activeTrackColor = ElectricMint,
                            inactiveTrackColor = CardSlate
                        )
                    )
                }
            }
        }

        // Smart suppression info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Smart suppression",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = SlateTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "When no items are pending for the current hour, reminders are automatically silenced to reduce notification fatigue.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                }
            }
        }

        // Test notification
        item {
            Button(
                onClick = onTriggerTestNotification,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricMint),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Test notification",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.Black
                )
            }
        }
    }
}
