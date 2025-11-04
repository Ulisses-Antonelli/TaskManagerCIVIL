package com.project.taskmanagercivil.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.project.taskmanagercivil.domain.models.AppTheme

// Tema Padrão - Azul e Verde (Material Design)
private val DefaultLightColorScheme = lightColorScheme(
    // Paleta padrão refinada: azul médio com verde secundário para sensação corporativa clara
    primary = Color(0xFF1565C0),           // Indigo 600 - azul profissional
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEFFF),  // Azul muito claro para contêineres
    onPrimaryContainer = Color(0xFF001E3C),
    secondary = Color(0xFF2E7D32),         // Verde 700
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDFF5E0), // Verde claro suave
    onSecondaryContainer = Color(0xFF0B3B08),
    tertiary = Color(0xFFEF6C00),          // Laranja/amber para acentos
    onTertiary = Color.White,
    error = Color(0xFFD32F2F),             // Mantido padrão Material
    background = Color(0xFFF7FAFC),        // Muito suave, quase branco
    surface = Color.White,
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFECEFF6),
    onSurfaceVariant = Color(0xFF4B5563)
)

// Paleta Verde-Azul Suave (#b9d8c2, #9ac2c9, #8aa1b1, #4a5043, #ffcb47)
private val Palette1ColorScheme = lightColorScheme(
    // Paleta verde-azulada suave: foco em tons calmantes para áreas administrativas
    primary = Color(0xFF3A9D8F),           // Teal/verde-azulado com contraste
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBEEDE3),  // Verde água suave
    onPrimaryContainer = Color(0xFF042923),
    secondary = Color(0xFF7B8FA0),         // Azul acinzentado (mantido)
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD4E4D9),
    onSecondaryContainer = Color(0xFF2D3E35),
    tertiary = Color(0xFFE5B940),          // Amarelo/âmbar para destaque
    onTertiary = Color(0xFF3A3120),
    error = Color(0xFFD32F2F),
    background = Color(0xFFF7FAF8),
    surface = Color.White,
    onBackground = Color(0xFF21302A),
    onSurface = Color(0xFF21302A),
    surfaceVariant = Color(0xFFE8EDE9),
    onSurfaceVariant = Color(0xFF5A6B5E)
)

// Paleta Azul Escuro e Laranja (#000000, #14213d, #fca311, #e5e5e5, #ffffff)
private val NavyOrangeColorScheme = lightColorScheme(
    // Paleta Navy + Laranja: alto contraste para alertas e ações
    primary = Color(0xFF0B2545),           // Azul marinho mais profundo
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE7F6),  // azul bem claro para contêineres
    onPrimaryContainer = Color(0xFF042038),
    secondary = Color(0xFFF59E0B),         // Amber/laranja caloroso
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF3DB), // laranja bem claro
    onSecondaryContainer = Color(0xFF3D2D00),
    tertiary = Color(0xFF2A4A6F),          // Azul médio para variações
    onTertiary = Color.White,
    error = Color(0xFFBA1A1A),
    background = Color(0xFFF4F6F9),        // Cinza muito claro
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F1724),
    onSurface = Color(0xFF0F1724),
    surfaceVariant = Color(0xFFDEE3EA),
    onSurfaceVariant = Color(0xFF42474E)
)

// Tema Escuro
private val DarkColorScheme = darkColorScheme(
    // Tema escuro: tons frios para reduzir fadiga ocular
    primary = Color(0xFF7FB6D9),           // Azul claro sobre fundo escuro
    onPrimary = Color(0xFF002635),
    primaryContainer = Color(0xFF00384D),  // Azul bem escuro
    onPrimaryContainer = Color(0xFFBEE8F7),
    secondary = Color(0xFF88C9A8),         // Verde claro vibrante
    onSecondary = Color(0xFF00391F),
    secondaryContainer = Color(0xFF00522F),
    onSecondaryContainer = Color(0xFFA4E5C3),
    tertiary = Color(0xFFFFB874),          // Laranja suave para acentos
    onTertiary = Color(0xFF3D2D00),
    error = Color(0xFFFFB4AB),
    background = Color(0xFF0F1724),
    surface = Color(0xFF111827),
    onBackground = Color(0xFFE6EDF5),
    onSurface = Color(0xFFE6EDF5),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFCBD5E1)
)

@Composable
fun TaskManagerTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.DEFAULT -> DefaultLightColorScheme
        AppTheme.PALETTE_1 -> Palette1ColorScheme
        AppTheme.NAVY_ORANGE -> NavyOrangeColorScheme
        AppTheme.DARK -> DarkColorScheme
    }

    val extendedColors = when (appTheme) {
        AppTheme.DEFAULT -> DefaultLightExtendedColorScheme
        AppTheme.PALETTE_1 -> Palette1ExtendedColorScheme
        AppTheme.NAVY_ORANGE -> NavyOrangeExtendedColorScheme
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