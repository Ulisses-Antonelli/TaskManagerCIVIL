package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.Role
import com.project.taskmanagercivil.domain.models.User

/**
 * Componente de tabela para gerenciamento de usuários
 * Com edição inline de roles e status
 */
@Composable
fun UserManagementTable(
    users: List<User>,
    onUserClick: (String) -> Unit = {},
    onRolesChange: (String, List<Role>) -> Unit = { _, _ -> },
    onStatusChange: (String, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Gerenciamento de Usuários",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Cabeçalhos da tabela
            TableHeader()

            Spacer(modifier = Modifier.height(8.dp))

            // Linhas da tabela
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(users) { user ->
                    UserTableRow(
                        user = user,
                        onUserClick = onUserClick,
                        onRolesChange = onRolesChange,
                        onStatusChange = onStatusChange
                    )
                }
            }
        }
    }
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("Usuário", Modifier.weight(1.5f))

        VerticalDivider(
            modifier = Modifier
                .height(20.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Email", Modifier.weight(2f))

        VerticalDivider(
            modifier = Modifier
                .height(20.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Papéis", Modifier.weight(2f))

        VerticalDivider(
            modifier = Modifier
                .height(20.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Status", Modifier.weight(1f))

        VerticalDivider(
            modifier = Modifier
                .height(20.dp)
                .padding(horizontal = 4.dp)
        )

        HeaderCell("Ações", Modifier.weight(0.8f))
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun UserTableRow(
    user: User,
    onUserClick: (String) -> Unit,
    onRolesChange: (String, List<Role>) -> Unit,
    onStatusChange: (String, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Usuário (clicável)
        Row(
            modifier = Modifier
                .weight(1.5f)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onUserClick(user.id) }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Avatar
            UserAvatar(user = user, size = 32.dp)

            Text(
                text = user.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Email
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(2f)
        )

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Papéis (menu suspenso)
        RolesSelector(
            user = user,
            onRolesChange = { newRoles -> onRolesChange(user.id, newRoles) },
            modifier = Modifier.weight(2f)
        )

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Status (menu suspenso)
        StatusSelector(
            isActive = user.isActive,
            onStatusChange = { newStatus -> onStatusChange(user.id, newStatus) },
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Ações
        Text(
            text = "Ver detalhes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .weight(0.8f)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onUserClick(user.id) }
                .padding(4.dp)
        )
    }
}

@Composable
private fun RolesSelector(
    user: User,
    onRolesChange: (List<Role>) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedRoles by remember { mutableStateOf(user.roles) }

    Box(modifier = modifier) {
        // Botão para exibir roles atuais
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .clickable { expanded = true }
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedRoles.joinToString(", ") { it.displayName },
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Selecionar papéis",
                modifier = Modifier.size(16.dp)
            )
        }

        // Menu suspenso com checkboxes
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Role.entries.forEach { role ->
                val isSelected = selectedRoles.contains(role)

                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Column {
                                Text(
                                    text = role.displayName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = role.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    onClick = {
                        selectedRoles = if (isSelected) {
                            // Garante que pelo menos um role permanece
                            if (selectedRoles.size > 1) {
                                selectedRoles - role
                            } else {
                                selectedRoles
                            }
                        } else {
                            selectedRoles + role
                        }
                        onRolesChange(selectedRoles)
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusSelector(
    isActive: Boolean,
    onStatusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Badge de status atual
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .clickable { expanded = true },
            shape = RoundedCornerShape(4.dp),
            color = if (isActive) {
                Color(0xFF4CAF50).copy(alpha = 0.15f)
            } else {
                Color(0xFF9E9E9E).copy(alpha = 0.15f)
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                    )
                    Text(
                        text = if (isActive) "Ativo" else "Inativo",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (isActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Alterar status",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Menu suspenso
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Ativar usuário")
                    }
                },
                onClick = {
                    onStatusChange(true)
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Inativar usuário")
                    }
                },
                onClick = {
                    onStatusChange(false)
                    expanded = false
                }
            )
        }
    }
}
