package com.taskplanner.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DeepSlate = Color(0xFF1E293B)
val DarkSlateBackground = Color(0xFF0F172A)
val CardSlate = Color(0xFF334155)
val CardSlateBorder = Color(0xFF475569)
val ElectricMint = Color(0xFF10B981)
val ElectricMintContainer = Color(0xFF065F46)
val AlertAmber = Color(0xFFF59E0B)
val AlertAmberContainer = Color(0xFF78350F)
val SlateTextPrimary = Color(0xFFF8FAFC)
val SlateTextSecondary = Color(0xFF94A3B8)
val PriorityHighRed = Color(0xFFEF4444)
val SurfaceElevated = Color(0xFF1A2332)

private val ChronoDoDarkColorScheme = darkColorScheme(
    primary = ElectricMint,
    onPrimary = Color.Black,
    primaryContainer = ElectricMintContainer,
    onPrimaryContainer = Color.White,
    secondary = AlertAmber,
    onSecondary = Color.Black,
    secondaryContainer = AlertAmberContainer,
    onSecondaryContainer = Color.White,
    background = DarkSlateBackground,
    onBackground = SlateTextPrimary,
    surface = DeepSlate,
    onSurface = SlateTextPrimary,
    surfaceVariant = CardSlate,
    onSurfaceVariant = SlateTextSecondary,
    outline = CardSlateBorder
)

private val ChronoDoTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.25).sp,
        color = SlateTextPrimary
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp,
        color = SlateTextPrimary
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp,
        color = SlateTextPrimary
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp,
        color = SlateTextPrimary
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp,
        color = SlateTextPrimary
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = SlateTextPrimary
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = SlateTextPrimary
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = SlateTextPrimary
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        color = SlateTextSecondary
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = SlateTextPrimary
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = SlateTextSecondary
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = SlateTextSecondary
    )
)

@Composable
fun TaskPlannerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ChronoDoDarkColorScheme,
        typography = ChronoDoTypography,
        content = content
    )
}
