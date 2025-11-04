package com.project.taskmanagercivil.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.AppTheme
import com.project.taskmanagercivil.domain.models.ThemeSettings
import com.project.taskmanagercivil.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar o tema da aplicação
 */
class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val _themeSettings = MutableStateFlow(ThemeSettings())
    val themeSettings: StateFlow<ThemeSettings> = _themeSettings.asStateFlow()

    init {
        loadThemeSettings()
    }

    private fun loadThemeSettings() {
        viewModelScope.launch {
            themeRepository.getThemeSettings().collect { settings ->
                _themeSettings.value = settings
            }
        }
    }

    /**
     * Altera o tema da aplicação
     */
    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch {
            themeRepository.saveTheme(theme)
        }
    }
}
