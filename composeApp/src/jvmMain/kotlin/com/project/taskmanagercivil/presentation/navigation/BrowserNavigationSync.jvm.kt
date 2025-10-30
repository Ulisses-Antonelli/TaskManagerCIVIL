package com.project.taskmanagercivil.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

/**
 * Implementação JVM/Desktop - não faz nada pois Desktop não tem URL do navegador
 */
@Composable
actual fun BrowserNavigationSync(navController: NavHostController) {
    // Não faz nada no Desktop
}
