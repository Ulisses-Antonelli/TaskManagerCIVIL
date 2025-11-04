package com.project.taskmanagercivil.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.project.taskmanagercivil.domain.models.AppTheme

// Tema Padrão - Light
private val DefaultLightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFF388E3C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9),
    tertiary = Color(0xFFF57C00),
    error = Color(0xFFD32F2F),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

// Paleta 1 - Verde-Azul (#b9d8c2, #9ac2c9, #8aa1b1, #4a5043, #ffcb47)
private val Palette1ColorScheme = lightColorScheme(
    primary = Color(0xFF9ac2c9),           // Azul suave
    onPrimary = Color(0xFF4a5043),         // Cinza escuro
    primaryContainer = Color(0xFFb9d8c2),  // Verde claro
    secondary = Color(0xFF8aa1b1),         // Azul acinzentado
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFb9d8c2),
    tertiary = Color(0xFFffcb47),          // Amarelo
    onTertiary = Color(0xFF4a5043),
    error = Color(0xFFD32F2F),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onBackground = Color(0xFF4a5043),
    onSurface = Color(0xFF4a5043),
)

// Tema Escuro
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6B9CAE),           // Azul médio escurecido
    onPrimary = Color(0xFFE0E0E0),
    primaryContainer = Color(0xFF3A4D52),  // Azul escuro
    secondary = Color(0xFF7BA591),         // Verde escurecido
    onSecondary = Color(0xFFE0E0E0),
    secondaryContainer = Color(0xFF2D3E35),
    tertiary = Color(0xFFFFB74D),          // Amarelo suave
    onTertiary = Color(0xFF1C1C1C),
    error = Color(0xFFEF5350),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF2D2D30),
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
)

@Composable
fun TaskManagerTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.DEFAULT -> DefaultLightColorScheme
        AppTheme.PALETTE_1 -> Palette1ColorScheme
        AppTheme.DARK -> DarkColorScheme
    }

    val extendedColors = when (appTheme) {
        AppTheme.DEFAULT -> DefaultLightExtendedColorScheme
        AppTheme.PALETTE_1 -> Palette1ExtendedColorScheme
        AppTheme.DARK -> DarkExtendedColorScheme
    }

    CompositionLocalProvider(LocalExtendedColorScheme provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}