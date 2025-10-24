package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * Layout padrão para todas as telas do sistema
 * Inclui automaticamente o breadcrumb dinâmico
 *
 * @param navController Controller de navegação para o breadcrumb
 * @param content Conteúdo da tela
 */
@Composable
fun ScreenLayout(
    navController: NavController,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Breadcrumb dinâmico no topo
        DynamicBreadcrumbs(
            navController = navController
        )

        // Divisor
        HorizontalDivider()

        // Conteúdo da tela
        content()
    }
}
