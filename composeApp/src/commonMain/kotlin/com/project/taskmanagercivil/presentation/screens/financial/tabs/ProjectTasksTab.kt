package com.project.taskmanagercivil.presentation.screens.financial.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.ProjectFinancials
import com.project.taskmanagercivil.presentation.components.FinancialTasksTable
import com.project.taskmanagercivil.presentation.screens.financial.toTaskRows

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectTasksTab(projectFinancials: ProjectFinancials) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Filtros
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Filtro Disciplina
            var disciplinaExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = disciplinaExpanded,
                onExpandedChange = { disciplinaExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = "Todas",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Disciplina", style = MaterialTheme.typography.labelSmall) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = disciplinaExpanded) },
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = disciplinaExpanded,
                    onDismissRequest = { disciplinaExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Todas") }, onClick = { disciplinaExpanded = false })
                    DropdownMenuItem(text = { Text("Estrutural") }, onClick = { disciplinaExpanded = false })
                    DropdownMenuItem(text = { Text("Elétrica") }, onClick = { disciplinaExpanded = false })
                    DropdownMenuItem(text = { Text("Arquitetura") }, onClick = { disciplinaExpanded = false })
                    DropdownMenuItem(text = { Text("Hidráulica") }, onClick = { disciplinaExpanded = false })
                }
            }

            // Filtro Responsável
            var responsavelExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = responsavelExpanded,
                onExpandedChange = { responsavelExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = "Todos",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Responsável", style = MaterialTheme.typography.labelSmall) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = responsavelExpanded) },
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = responsavelExpanded,
                    onDismissRequest = { responsavelExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Todos") }, onClick = { responsavelExpanded = false })
                    DropdownMenuItem(text = { Text("Marcos") }, onClick = { responsavelExpanded = false })
                    DropdownMenuItem(text = { Text("Carla") }, onClick = { responsavelExpanded = false })
                    DropdownMenuItem(text = { Text("Pedro") }, onClick = { responsavelExpanded = false })
                }
            }

            // Filtro Status
            var statusExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = "Todos",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status", style = MaterialTheme.typography.labelSmall) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Todos") }, onClick = { statusExpanded = false })
                    DropdownMenuItem(text = { Text("Concluída") }, onClick = { statusExpanded = false })
                    DropdownMenuItem(text = { Text("Em curso") }, onClick = { statusExpanded = false })
                    DropdownMenuItem(text = { Text("Em revisão") }, onClick = { statusExpanded = false })
                    DropdownMenuItem(text = { Text("Atrasada") }, onClick = { statusExpanded = false })
                }
            }
        }

        // Tabela de Tarefas da Obra
        // TODO: Este tab precisa receber a lista filtrada de tarefas do ViewModel
        // Por enquanto mostra uma mensagem placeholder
        Text(
            text = "Carregando tarefas do projeto...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}
