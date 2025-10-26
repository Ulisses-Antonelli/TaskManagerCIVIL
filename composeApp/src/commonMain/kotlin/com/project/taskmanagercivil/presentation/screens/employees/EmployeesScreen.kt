package com.project.taskmanagercivil.presentation.screens.employees

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.navigation.NavigationState
import com.project.taskmanagercivil.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreenContent(
    navController: NavController,
    viewModel: EmployeesViewModel,
    onEmployeeClick: (String) -> Unit,
    onCreateEmployee: () -> Unit = {},
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "users",
            onMenuClick = onNavigate
        )

        HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Estado do modal (antes do Scaffold para ser acessível no TopAppBar)
            var showEmployeeFormModal by remember { mutableStateOf(false) }
            var employeeToEdit by remember { mutableStateOf<Employee?>(null) }

            Scaffold(
                topBar = {
                    Column {
                        TopAppBar(
                            title = { Text("Colaboradores") }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DynamicBreadcrumbs(
                                navController = navController,
                                currentRoot = NavigationState.currentRoot,
                                modifier = Modifier.weight(1f)
                            )

                            // Botão de adicionar (alinhado com breadcrumbs)
                            IconButton(
                                onClick = {
                                    employeeToEdit = null
                                    showEmployeeFormModal = true
                                },
                                modifier = Modifier.size(40.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Criar novo colaborador",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Barra de pesquisa
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Filtros
                    FiltersRow(
                        uiState = uiState,
                        viewModel = viewModel,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lista de colaboradores
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        uiState.filteredEmployees.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Nenhum colaborador encontrado",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tente ajustar os filtros",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        else -> {
                            EmployeesList(
                                employees = uiState.filteredEmployees,
                                onEmployeeClick = onEmployeeClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Modal de formulário de colaborador
                    if (showEmployeeFormModal) {
                        EmployeeFormModal(
                            employee = employeeToEdit,
                            availableProjects = uiState.allProjects,
                            onDismiss = {
                                showEmployeeFormModal = false
                                employeeToEdit = null
                            },
                            onSave = { employee, projectIds ->
                                viewModel.saveEmployee(employee)
                                showEmployeeFormModal = false
                                employeeToEdit = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar por nome, cargo ou email...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Pesquisar"
            )
        },
        singleLine = true,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersRow(
    uiState: EmployeesUiState,
    viewModel: EmployeesViewModel,
    modifier: Modifier = Modifier
) {
    // Estados para controlar abertura dos dropdowns
    var statusExpanded by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Linha com os 2 dropdowns
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dropdown 1: Status
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = when (uiState.filterStatus) {
                        EmployeeFilterStatus.ALL -> "Todos os Status"
                        EmployeeFilterStatus.ACTIVE -> "Ativos"
                        EmployeeFilterStatus.INACTIVE -> "Demitidos"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
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
                                viewModel.onFilterStatusChange(status)
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            // Dropdown 2: Ordenar por
            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.sortOrder.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ordenar por") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    EmployeeSortOrder.entries.forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order.displayName) },
                            onClick = {
                                viewModel.onSortOrderChange(order)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Botão para limpar filtros (se houver filtros ativos)
        if (uiState.filterStatus != EmployeeFilterStatus.ALL) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { viewModel.onFilterStatusChange(EmployeeFilterStatus.ALL) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Limpar Filtros")
            }
        }
    }
}

@Composable
private fun EmployeesList(
    employees: List<Employee>,
    onEmployeeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(employees, key = { it.id }) { employee ->
            EmployeeCard(
                employee = employee,
                onClick = { onEmployeeClick(employee.id) }
            )
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
