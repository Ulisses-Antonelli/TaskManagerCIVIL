package com.project.taskmanagercivil.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

/**
 * Sincroniza o Navigation Compose com a URL do navegador (apenas no Wasm)
 * Em outras plataformas, esta função não faz nada
 */
@Composable
expect fun BrowserNavigationSync(navController: NavHostController)
