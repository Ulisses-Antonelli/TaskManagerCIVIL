package com.project.taskmanagercivil.domain.repository

import com.project.taskmanagercivil.domain.models.AppTheme
import com.project.taskmanagercivil.domain.models.ThemeSettings
import kotlinx.coroutines.flow.Flow

/**
 * Interface para gerenciar as configurações de tema da aplicação
 */
interface ThemeRepository {
    /**
     * Obtém as configurações de tema atuais como Flow
     */
    fun getThemeSettings(): Flow<ThemeSettings>

    /**
     * Salva o tema selecionado
     */
    suspend fun saveTheme(theme: AppTheme): Result<Unit>
}
