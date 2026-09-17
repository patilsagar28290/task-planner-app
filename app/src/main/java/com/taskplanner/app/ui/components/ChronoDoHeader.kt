package com.taskplanner.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskplanner.app.ui.theme.AlertAmber
import com.taskplanner.app.ui.theme.CardSlate
import com.taskplanner.app.ui.theme.CardSlateBorder
import com.taskplanner.app.ui.theme.DarkSlateBackground
import com.taskplanner.app.ui.theme.DeepSlate
import com.taskplanner.app.ui.theme.ElectricMint
import com.taskplanner.app.ui.theme.SlateTextSecondary
import com.taskplanner.app.ui.theme.SurfaceElevated
import java.util.Calendar
import java.util.Locale

@Composable
fun ChronoDoHeader(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    activePendingCount: Int,
    startHour: Int,
    endHour: Int
) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val formattedCurrentTime = String.format(Locale.getDefault(), "%02d:00", currentHour)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSlateBackground)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
    ) {
        // Brand row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChronoDoLogoIcon()
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "My Assistant",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "by sagar patil",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateTextSecondary
                    )
                }
            }

            // Current time badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Dot indicators
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    val activeDot = currentHour % 4
                    for (i in 0..3) {
                        Box(
                            modifier = Modifier
                                .size(if (i == activeDot) 6.dp else 4.dp)
                                .clip(CircleShape)
                                .background(if (i == activeDot) ElectricMint else CardSlateBorder.copy(alpha = 0.4f))
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = formattedCurrentTime,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = AlertAmber
                    )
                }
            }
        }

        // Tab bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = ElectricMint,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ElectricMint,
                    height = 2.dp
                )
            },
            divider = {}
        ) {
            listOf("Tasks", "Completed", "Settings").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selectedTab == index) ElectricMint else SlateTextSecondary
                            )
                            if (index == 0 && activePendingCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ElectricMint.copy(alpha = 0.15f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "$activePendingCount",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = ElectricMint
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ChronoDoLogoIcon(modifier: Modifier = Modifier.size(36.dp)) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .scale(pulseScale)
            .clip(CircleShape)
            .background(SurfaceElevated)
            .border(1.5.dp, ElectricMint.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(30.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2 - 3.dp.toPx()

            drawCircle(
                color = CardSlateBorder.copy(alpha = 0.3f),
                radius = radius,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Clock hand
            drawLine(
                color = ElectricMint,
                start = center,
                end = Offset(center.x, 7.dp.toPx()),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = ElectricMint,
            modifier = Modifier.size(14.dp)
        )
    }
}
