package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.domain.models.AppTheme
import com.project.taskmanagercivil.domain.models.ThemeSettings
import com.project.taskmanagercivil.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementação do ThemeRepository
 * Por enquanto armazena o tema em memória (MutableStateFlow)
 * Pode ser expandido para usar localStorage no futuro
 */
class ThemeRepositoryImpl : ThemeRepository {
    private val _themeSettings = MutableStateFlow(ThemeSettings())

    override fun getThemeSettings(): Flow<ThemeSettings> {
        return _themeSettings.asStateFlow()
    }

    override suspend fun saveTheme(theme: AppTheme): Result<Unit> {
        return try {
            _themeSettings.value = ThemeSettings(selectedTheme = theme)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
