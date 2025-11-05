package com.project.taskmanagercivil.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.project.taskmanagercivil.domain.models.AppTheme

// ============================================================================
// TEMA PADRÃO - Azul e Verde (Material Design)
// ============================================================================
private val DefaultLightColorScheme = lightColorScheme(
    // PRIMARY: Cor principal para botões primários, AppBar, elementos de destaque
    primary = Color(0xFF1565C0),           // #1565C0 - Azul profissional (Material Blue 800)
    onPrimary = Color.White,               // #FFFFFF - Texto sobre primary

    // PRIMARY CONTAINER: Backgrounds de elementos primários menos intensos
    primaryContainer = Color(0xFFcae2f6ff),  // #cae2f6ff - Azul muito claro
    onPrimaryContainer = Color(0xFF001E3C), // #001E3C - Texto sobre primaryContainer

    // SECONDARY: Cor secundária para FABs, checkboxes, switches
    secondary = Color(0xFF2E7D32),         // #2E7D32 - Verde (Material Green 700)
    onSecondary = Color.White,             // #FFFFFF - Texto sobre secondary

    // SECONDARY CONTAINER: Backgrounds de elementos secundários
    secondaryContainer = Color(0xFFDFF5E0), // #DFF5E0 - Verde claro suave
    onSecondaryContainer = Color(0xFF0B3B08), // #0B3B08 - Texto sobre secondaryContainer

    // TERTIARY: Cor terciária para acentos e detalhes
    tertiary = Color(0xFFEF6C00),          // #EF6C00 - Laranja/amber
    onTertiary = Color.White,              // #FFFFFF - Texto sobre tertiary

    // ERROR: Cor para erros, alertas, ações destrutivas
    error = Color(0xFFD32F2F),             // #D32F2F - Vermelho (Material Red 700)

    // BACKGROUND: Fundo principal da aplicação (sidebar, telas)
    background = Color(0xFFdae3eaff),        // #dae3eaff - Branco levemente azulado

    // SURFACE: Fundo de cards, modais, componentes elevados
    surface = Color(0xFFE6E9EC),           // #E6E9EC - Cinza 10% mais escuro que background

    // TEXTOS
    onBackground = Color(0xFF111827),      // #111827 - Texto sobre background
    onSurface = Color(0xFF111827),         // #111827 - Texto sobre surface

    // SURFACE VARIANT: Variação de surface para contraste sutil
    surfaceVariant = Color(0xFFECEFF6),    // #ECEFF6 - Cinza azulado claro
    onSurfaceVariant = Color(0xFF4B5563)   // #4B5563 - Texto sobre surfaceVariant
)

// ============================================================================
// PALETA VERDE-AZUL SUAVE
// Baseada nas cores: #b9d8c2, #9ac2c9, #8aa1b1, #4a5043, #ffcb47
// ============================================================================
private val Palette1ColorScheme = lightColorScheme(
    // PRIMARY: Teal/verde-azulado para sensação calmante
    primary = Color(0xFF3A9D8F),           // #3A9D8F - Teal com bom contraste
    onPrimary = Color.White,               // #FFFFFF - Texto sobre primary

    // PRIMARY CONTAINER: Verde água suave
    primaryContainer = Color(0xFFBEEDE3),  // #BEEDE3 - Verde água claro
    onPrimaryContainer = Color(0xFF042923), // #042923 - Texto sobre primaryContainer

    // SECONDARY: Azul acinzentado da paleta original
    secondary = Color(0xFF7B8FA0),         // #7B8FA0 - Azul acinzentado (#8aa1b1 escurecido)
    onSecondary = Color.White,             // #FFFFFF - Texto sobre secondary

    // SECONDARY CONTAINER: Verde claro suave
    secondaryContainer = Color(0xFFD4E4D9), // #D4E4D9 - Verde claro
    onSecondaryContainer = Color(0xFF2D3E35), // #2D3E35 - Texto sobre secondaryContainer

    // TERTIARY: Amarelo/âmbar da paleta original para destaque
    tertiary = Color(0xFFE5B940),          // #E5B940 - Amarelo saturado (#ffcb47 ajustado)
    onTertiary = Color(0xFF3A3120),        // #3A3120 - Texto sobre tertiary

    // ERROR: Vermelho para alertas
    error = Color(0xFFD32F2F),             // #D32F2F - Vermelho padrão

    // BACKGROUND: Fundo principal (sidebar, telas)
    background = Color(0xFFF7FAF8),        // #F7FAF8 - Branco levemente esverdeado

    // SURFACE: Fundo de cards e componentes elevados
    surface = Color(0xFFE6E9E6),           // #E6E9E6 - Cinza esverdeado 10% mais escuro

    // TEXTOS
    onBackground = Color(0xFF21302A),      // #21302A - Texto sobre background
    onSurface = Color(0xFF21302A),         // #21302A - Texto sobre surface

    // SURFACE VARIANT: Variação para contraste sutil
    surfaceVariant = Color(0xFFE8EDE9),    // #E8EDE9 - Cinza esverdeado claro
    onSurfaceVariant = Color(0xFF5A6B5E)   // #5A6B5E - Texto sobre surfaceVariant (#4a5043 clarificado)
)

// ============================================================================
// PALETA AZUL MARINHO E LARANJA
// Baseada nas cores: #000000, #14213d, #fca311, #e5e5e5, #ffffff
// ============================================================================
private val NavyOrangeColorScheme = lightColorScheme(
    // PRIMARY: Azul marinho profundo da paleta original
    primary = Color(0xFF0B2545),           // #0B2545 - Azul marinho (#14213d escurecido)
    onPrimary = Color.White,               // #FFFFFF - Texto sobre primary

    // PRIMARY CONTAINER: Azul bem claro
    primaryContainer = Color(0xFFDDE7F6),  // #DDE7F6 - Azul claro para containers
    onPrimaryContainer = Color(0xFF042038), // #042038 - Texto sobre primaryContainer

    // SECONDARY: Laranja vibrante da paleta original
    secondary = Color(0xFFF59E0B),         // #F59E0B - Laranja (#fca311 ajustado)
    onSecondary = Color.Black,             // #000000 - Texto sobre secondary

    // SECONDARY CONTAINER: Laranja bem claro
    secondaryContainer = Color(0xFFFFF3DB), // #FFF3DB - Laranja muito claro
    onSecondaryContainer = Color(0xFF3D2D00), // #3D2D00 - Texto sobre secondaryContainer

    // TERTIARY: Azul médio para variações
    tertiary = Color(0xFF2A4A6F),          // #2A4A6F - Azul médio
    onTertiary = Color.White,              // #FFFFFF - Texto sobre tertiary

    // ERROR: Vermelho para alertas
    error = Color(0xFFBA1A1A),             // #BA1A1A - Vermelho escuro

    // BACKGROUND: Fundo principal (sidebar, telas)
    background = Color(0xFFF4F6F9),        // #eaf2fdff - Cinza muito claro (#e5e5e5 clarificado)

    // SURFACE: Fundo de cards e componentes elevados
    surface = Color(0xFFE5E8ED),           // #E5E8ED - Cinza 10% mais escuro que background

    // TEXTOS
    onBackground = Color(0xFF0F1724),      // #0F1724 - Texto sobre background
    onSurface = Color(0xFF0F1724),         // #0F1724 - Texto sobre surface

    // SURFACE VARIANT: Variação para contraste
    surfaceVariant = Color(0xFFDEE3EA),    // #DEE3EA - Cinza azulado claro
    onSurfaceVariant = Color(0xFF42474E)   // #42474E - Texto sobre surfaceVariant
)

// ============================================================================
// TEMA ESCURO
// Tons frios para reduzir fadiga ocular
// ============================================================================
private val DarkColorScheme = darkColorScheme(
    // PRIMARY: Azul claro vibrante sobre fundo escuro
    primary = Color(0xFF7FB6D9),           // #7FB6D9 - Azul claro
    onPrimary = Color(0xFF002635),         // #002635 - Texto sobre primary

    // PRIMARY CONTAINER: Azul bem escuro
    primaryContainer = Color(0xFF00384D),  // #00384D - Azul escuro
    onPrimaryContainer = Color(0xFFBEE8F7), // #BEE8F7 - Texto sobre primaryContainer

    // SECONDARY: Verde claro vibrante
    secondary = Color(0xFF88C9A8),         // #88C9A8 - Verde claro
    onSecondary = Color(0xFF00391F),       // #00391F - Texto sobre secondary

    // SECONDARY CONTAINER: Verde escuro
    secondaryContainer = Color(0xFF00522F), // #00522F - Verde escuro
    onSecondaryContainer = Color(0xFFA4E5C3), // #A4E5C3 - Texto sobre secondaryContainer

    // TERTIARY: Laranja suave para acentos
    tertiary = Color(0xFFFFB874),          // #FFB874 - Laranja suave
    onTertiary = Color(0xFF3D2D00),        // #3D2D00 - Texto sobre tertiary

    // ERROR: Vermelho claro para alertas
    error = Color(0xFFFFB4AB),             // #FFB4AB - Vermelho claro

    // BACKGROUND: Fundo principal MUITO ESCURO (sidebar)
    background = Color(0xFF0A1220),        // #0A1220 - Azul muito escuro (quase preto)

    // SURFACE: Fundo de cards - azul escuro com melhor contraste
    surface = Color(0xFF1A2332),           // #1A2332 - Azul escuro (mais claro que background)

    // TEXTOS
    onBackground = Color(0xFFE6EDF5),      // #E6EDF5 - Texto sobre background
    onSurface = Color(0xFFE6EDF5),         // #E6EDF5 - Texto sobre surface

    // SURFACE VARIANT: Variação de surface
    surfaceVariant = Color(0xFF1F2937),    // #1F2937 - Cinza escuro
    onSurfaceVariant = Color(0xFFCBD5E1)   // #CBD5E1 - Texto sobre surfaceVariant
)

@Composable
fun TaskManagerTheme(
    appTheme: AppTheme = AppTheme.DARK,
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
