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

val DefaultLightExtendedColorScheme = ExtendedColorScheme(
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

val Palette1ExtendedColorScheme = ExtendedColorScheme(
    priorityCritical = Color(0xFFD32F2F),
    priorityHigh = Color(0xFFE5B940),      // Amarelo saturado
    priorityMedium = Color(0xFF7B8FA0),    // Azul acinzentado
    priorityLow = Color(0xFF6B9CA8),       // Azul suave
    statusTodo = Color(0xFF5A6B5E),        // Cinza escuro
    statusInProgress = Color(0xFF6B9CA8),  // Azul suave
    statusInReview = Color(0xFF7B8FA0),    // Azul acinzentado
    statusCompleted = Color(0xFF88B297),   // Verde médio
    statusBlocked = Color(0xFFD32F2F)
)

val NavyOrangeExtendedColorScheme = ExtendedColorScheme(
    priorityCritical = Color(0xFFBA1A1A),
    priorityHigh = Color(0xFFfca311),      // Laranja vibrante
    priorityMedium = Color(0xFF2A4A6F),    // Azul médio
    priorityLow = Color(0xFF4A6FA5),       // Azul claro
    statusTodo = Color(0xFF6B7280),        // Cinza
    statusInProgress = Color(0xFF14213d),  // Azul marinho
    statusInReview = Color(0xFF2A4A6F),    // Azul médio
    statusCompleted = Color(0xFF059669),   // Verde
    statusBlocked = Color(0xFFBA1A1A)
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

internal val LocalExtendedColorScheme = staticCompositionLocalOf { DefaultLightExtendedColorScheme }

val MaterialTheme.extendedColors: ExtendedColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColorScheme.current