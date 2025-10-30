package com.project.taskmanagercivil.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import kotlinx.browser.window

/**
 * Sincroniza o Navigation Compose com a URL do navegador
 * - Atualiza a URL quando a navegação interna muda
 * - Responde aos botões voltar/avançar do navegador
 */
@Composable
actual fun BrowserNavigationSync(navController: NavHostController) {
    // Monitora mudanças na navegação e atualiza a URL do navegador
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val route = destination.route ?: "dashboard"
            val currentHash = window.location.hash.removePrefix("#/")

            // Só atualiza se a rota mudou
            if (currentHash != route) {
                window.history.pushState(null, "", "#/$route")
            }
        }
    }

    // Sincroniza a rota inicial com a URL atual
    LaunchedEffect(Unit) {
        val hash = window.location.hash.removePrefix("#/")
        if (hash.isNotEmpty() && hash != "login") {
            try {
                navController.navigate(hash) {
                    launchSingleTop = true
                }
            } catch (e: Exception) {
                println("Erro ao navegar para rota inicial: $hash - ${e.message}")
            }
        }
    }
}
