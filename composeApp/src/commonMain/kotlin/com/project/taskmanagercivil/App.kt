package com.project.taskmanagercivil

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.project.taskmanagercivil.presentation.ViewModelFactory
import com.project.taskmanagercivil.presentation.navigation.AppNavigation
import com.project.taskmanagercivil.presentation.theme.TaskManagerTheme

@Composable
fun App() {
    val themeViewModel = ViewModelFactory.getThemeViewModel()
    val themeSettings by themeViewModel.themeSettings.collectAsState()

    TaskManagerTheme(appTheme = themeSettings.selectedTheme) {
        AppNavigation()
    }
}
