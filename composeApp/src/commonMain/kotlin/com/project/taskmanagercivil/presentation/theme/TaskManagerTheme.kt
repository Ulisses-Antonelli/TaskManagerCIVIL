package com.project.taskmanagercivil.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.project.taskmanagercivil.domain.models.AppTheme

// Tema Padrão - Azul e Verde (Material Design)
private val DefaultLightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),           // Azul principal
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E9FF),  // Azul claro mais suave (melhor contraste)
    onPrimaryContainer = Color(0xFF003258),
    secondary = Color(0xFF388E3C),         // Verde
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5EDD8), // Verde claro mais suave
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = Color(0xFFF57C00),
    onTertiary = Color.White,
    error = Color(0xFFD32F2F),
    background = Color(0xFFFAFAFA),        // Branco quente
    surface = Color.White,
    onBackground = Color(0xFF1A1C1E),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F)
)

// Paleta Verde-Azul Suave (#b9d8c2, #9ac2c9, #8aa1b1, #4a5043, #ffcb47)
private val Palette1ColorScheme = lightColorScheme(
    primary = Color(0xFF6B9CA8),           // Azul mais saturado para melhor contraste
    onPrimary = Color.White,
    primaryContainer = Color(0xFFb9d8c2),  // Verde claro
    onPrimaryContainer = Color(0xFF2D3E35),
    secondary = Color(0xFF7B8FA0),         // Azul acinzentado mais escuro
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD4E4D9),
    onSecondaryContainer = Color(0xFF2D3E35),
    tertiary = Color(0xFFE5B940),          // Amarelo mais saturado
    onTertiary = Color(0xFF3A3120),
    error = Color(0xFFD32F2F),
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    onBackground = Color(0xFF4a5043),
    onSurface = Color(0xFF4a5043),
    surfaceVariant = Color(0xFFE8EDE9),
    onSurfaceVariant = Color(0xFF5A6B5E)
)

// Paleta Azul Escuro e Laranja (#000000, #14213d, #fca311, #e5e5e5, #ffffff)
private val NavyOrangeColorScheme = lightColorScheme(
    primary = Color(0xFF14213d),           // Azul marinho
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1D5DB),  // Cinza claro
    onPrimaryContainer = Color(0xFF14213d),
    secondary = Color(0xFFfca311),         // Laranja vibrante
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFFFE8C5), // Laranja bem claro
    onSecondaryContainer = Color(0xFF3D2D00),
    tertiary = Color(0xFF2A4A6F),          // Azul médio
    onTertiary = Color.White,
    error = Color(0xFFBA1A1A),
    background = Color(0xFFe5e5e5),        // Cinza claro
    surface = Color(0xFFffffff),           // Branco
    onBackground = Color(0xFF1A1C1E),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDEE3EA),
    onSurfaceVariant = Color(0xFF42474E)
)

// Tema Escuro
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7DB8CC),           // Azul claro mais vibrante
    onPrimary = Color(0xFF003547),
    primaryContainer = Color(0xFF004D65),  // Azul escuro com contraste
    onPrimaryContainer = Color(0xFFB8E7F5),
    secondary = Color(0xFF88C9A8),         // Verde claro mais vibrante
    onSecondary = Color(0xFF00391F),
    secondaryContainer = Color(0xFF00522F),
    onSecondaryContainer = Color(0xFFA4E5C3),
    tertiary = Color(0xFFFFB874),          // Laranja suave
    onTertiary = Color(0xFF3D2D00),
    error = Color(0xFFFFB4AB),
    background = Color(0xFF1A1C1E),
    surface = Color(0xFF2D2F31),
    onBackground = Color(0xFFE3E2E6),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C6CF)
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