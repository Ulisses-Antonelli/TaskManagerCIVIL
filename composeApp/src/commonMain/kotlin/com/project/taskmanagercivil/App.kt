package com.project.taskmanagercivil

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.project.taskmanagercivil.presentation.navigation.AppNavigation

@Composable
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}
