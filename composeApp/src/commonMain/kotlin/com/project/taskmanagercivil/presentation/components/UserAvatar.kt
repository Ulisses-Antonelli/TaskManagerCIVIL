package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.taskmanagercivil.domain.models.User

/**
 * Componente de avatar simples do usuário
 */
@Composable
fun UserAvatar(
    user: User,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val initials = user.name.split(" ")
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = (size.value / 2.5).sp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Componente de avatar do usuário com menu suspenso
 * Exibe avatar, nome, email e ícone de configurações
 *
 * @param user Usuário logado
 * @param onLogout Callback para fazer logout
 * @param onSettings Callback para ir para configurações
 * @param modifier Modificador opcional
 */
@Composable
fun UserMenuAvatar(
    user: User?,
    onLogout: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Botão principal com avatar e informações
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar (círculo com iniciais)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (user != null) {
                    // Mostra iniciais do nome
                    Text(
                        text = getInitials(user.name),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Nome e email
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user?.name ?: "Usuário",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = user?.email ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Ícone de configurações
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configurações",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        // Menu suspenso
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Opção: Configurações
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações",
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Configurações")
                    }
                },
                onClick = {
                    expanded = false
                    onSettings()
                }
            )

            // Divisor
            HorizontalDivider()

            // Opção: Logout
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sair",
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Sair")
                    }
                },
                onClick = {
                    expanded = false
                    onLogout()
                }
            )
        }
    }
}

/**
 * Extrai as iniciais de um nome completo
 * Exemplo: "João Silva" -> "JS"
 */
private fun getInitials(name: String): String {
    val parts = name.trim().split(" ")
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> {
            val first = parts.first().take(1)
            val last = parts.last().take(1)
            (first + last).uppercase()
        }
    }
}
