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
    DEFAULT("Tema Padrão"),
    PALETTE_1("Paleta Verde-Azul"),
    DARK("Tema Escuro")
}
