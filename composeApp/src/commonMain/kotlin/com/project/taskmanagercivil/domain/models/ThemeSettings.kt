package com.project.taskmanagercivil.domain.models

/**
 * Representa as configurações de tema da aplicação
 */
data class ThemeSettings(
    val selectedTheme: AppTheme = AppTheme.DEFAULT
)

/**
 * Temas disponíveis na aplicação
 */
enum class AppTheme(val displayName: String) {
    DEFAULT("Azul e Verde"),
    PALETTE_1("Verde-Azul Suave"),
    NAVY_ORANGE("Azul Marinho e Laranja"),
    DARK("Tema Escuro")
}
