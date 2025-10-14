package com.project.taskmanagercivil.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColorScheme(
    val priorityCritical: Color,
    val priorityHigh: Color,
    val priorityMedium: Color,
    val priorityLow: Color,
    val statusTodo: Color,
    val statusInProgress: Color,
    val statusInReview: Color,
    val statusCompleted: Color,
    val statusBlocked: Color
)

val LightExtendedColorScheme = ExtendedColorScheme(
    priorityCritical = Color(0xFFD32F2F),
    priorityHigh = Color(0xFFF57C00),
    priorityMedium = Color(0xFFFBC02D),
    priorityLow = Color(0xFF388E3C),
    statusTodo = Color(0xFF757575),
    statusInProgress = Color(0xFF1976D2),
    statusInReview = Color(0xFF9C27B0),
    statusCompleted = Color(0xFF388E3C),
    statusBlocked = Color(0xFFD32F2F)
)

val DarkExtendedColorScheme = ExtendedColorScheme(
    priorityCritical = Color(0xFFEF5350),
    priorityHigh = Color(0xFFFFB74D),
    priorityMedium = Color(0xFFFFF176),
    priorityLow = Color(0xFF81C784),
    statusTodo = Color(0xFFBDBDBD),
    statusInProgress = Color(0xFF90CAF9),
    statusInReview = Color(0xFFCE93D8),
    statusCompleted = Color(0xFF81C784),
    statusBlocked = Color(0xFFEF5350)
)

internal val LocalExtendedColorScheme = staticCompositionLocalOf { LightExtendedColorScheme }

val MaterialTheme.extendedColors: ExtendedColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColorScheme.current