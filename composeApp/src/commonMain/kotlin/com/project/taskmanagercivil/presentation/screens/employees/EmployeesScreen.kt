package com.project.taskmanagercivil.presentation.screens.employees

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.utils.FormatUtils

@Composable
fun EmployeesScreenContent(
    viewModel: EmployeesViewModel,
    onEmployeeClick: (String) -> Unit,
    onCreateEmployee: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "users",
            onMenuClick = onNavigate
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Colaboradores",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onCreateEmployee,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("+ Novo Colaborador")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            
            EmployeeFiltersSection(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                filterStatus = uiState.filterStatus,
                onFilterStatusChange = { viewModel.onFilterStatusChange(it) },
                sortOrder = uiState.sortOrder,
                onSortOrderChange = { viewModel.onSortOrderChange(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredEmployees.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum colaborador encontrado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.filteredEmployees,
                        key = { it.id }
                    ) { employee ->
                        EmployeeCard(
                            employee = employee,
                            onClick = { onEmployeeClick(employee.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmployeeFiltersSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterStatus: EmployeeFilterStatus,
    onFilterStatusChange: (EmployeeFilterStatus) -> Unit,
    sortOrder: EmployeeSortOrder,
    onSortOrderChange: (EmployeeSortOrder) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Campo de busca
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nome, cargo ou email...") },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Filtro de status
                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (filterStatus) {
                                EmployeeFilterStatus.ALL -> "Todos os Status"
                                EmployeeFilterStatus.ACTIVE -> "Ativos"
                                EmployeeFilterStatus.INACTIVE -> "Demitidos"
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        EmployeeFilterStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (status) {
                                            EmployeeFilterStatus.ALL -> "Todos os Status"
                                            EmployeeFilterStatus.ACTIVE -> "Ativos"
                                            EmployeeFilterStatus.INACTIVE -> "Demitidos"
                                        }
                                    )
                                },
                                onClick = {
                                    onFilterStatusChange(status)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Ordenação
                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = sortOrder.displayName)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        EmployeeSortOrder.entries.forEach { order ->
                            DropdownMenuItem(
                                text = { Text(order.displayName) },
                                onClick = {
                                    onSortOrderChange(order)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmployeeCard(
    employee: Employee,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar (iniciais)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.fullName.split(" ")
                        .take(2)
                        .joinToString("") { it.first().uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Informações principais
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = employee.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Badge de status
                    if (!employee.isCurrentlyActive()) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Inativo",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Text(
                    text = employee.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = employee.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Informações secundárias
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${employee.projectIds.size} projeto(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Desde ${FormatUtils.formatDate(employee.hireDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
