package com.project.taskmanagercivil.presentation.screens.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.models.TeamDepartment
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.navigation.NavigationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreenContent(
    navController: NavController,
    viewModel: TeamsViewModel,
    onTeamClick: (String) -> Unit,
    onCreateTeam: () -> Unit = {},
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "teams",
            onMenuClick = onNavigate
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Times",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onCreateTeam,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("+ Novo Time")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

           
            TeamFiltersSection(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                filterDepartment = uiState.filterDepartment,
                onFilterDepartmentChange = { viewModel.onFilterDepartmentChange(it) },
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
            } else if (uiState.filteredTeams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum time encontrado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.filteredTeams,
                        key = { it.id }
                    ) { team ->
                        TeamCard(
                            team = team,
                            onClick = { onTeamClick(team.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamFiltersSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterDepartment: TeamFilterDepartment,
    onFilterDepartmentChange: (TeamFilterDepartment) -> Unit,
    sortOrder: TeamSortOrder,
    onSortOrderChange: (TeamSortOrder) -> Unit
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
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nome ou descrição...") },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                
                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (filterDepartment) {
                                TeamFilterDepartment.ALL -> "Todos os Departamentos"
                                TeamFilterDepartment.ARCHITECTURE -> TeamDepartment.ARCHITECTURE.displayName
                                TeamFilterDepartment.STRUCTURE -> TeamDepartment.STRUCTURE.displayName
                                TeamFilterDepartment.HYDRAULIC -> TeamDepartment.HYDRAULIC.displayName
                                TeamFilterDepartment.ELECTRICAL -> TeamDepartment.ELECTRICAL.displayName
                                TeamFilterDepartment.MASONRY -> TeamDepartment.MASONRY.displayName
                                TeamFilterDepartment.FINISHING -> TeamDepartment.FINISHING.displayName
                                TeamFilterDepartment.CLEANING -> TeamDepartment.CLEANING.displayName
                                TeamFilterDepartment.SAFETY -> TeamDepartment.SAFETY.displayName
                                TeamFilterDepartment.ADMINISTRATION -> TeamDepartment.ADMINISTRATION.displayName
                                TeamFilterDepartment.PURCHASING -> TeamDepartment.PURCHASING.displayName
                                TeamFilterDepartment.QUALITY -> TeamDepartment.QUALITY.displayName
                                TeamFilterDepartment.PLANNING -> TeamDepartment.PLANNING.displayName
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos os Departamentos") },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.ALL)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.ARCHITECTURE.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.ARCHITECTURE)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.STRUCTURE.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.STRUCTURE)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.HYDRAULIC.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.HYDRAULIC)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.ELECTRICAL.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.ELECTRICAL)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.MASONRY.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.MASONRY)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.FINISHING.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.FINISHING)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.CLEANING.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.CLEANING)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.SAFETY.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.SAFETY)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.ADMINISTRATION.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.ADMINISTRATION)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.PURCHASING.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.PURCHASING)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.QUALITY.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.QUALITY)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(TeamDepartment.PLANNING.displayName) },
                            onClick = {
                                onFilterDepartmentChange(TeamFilterDepartment.PLANNING)
                                expanded = false
                            }
                        )
                    }
                }

                
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
                        TeamSortOrder.entries.forEach { order ->
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
private fun TeamCard(
    team: Team,
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
            
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(getDepartmentColor(team.department)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getDepartmentInitials(team.department),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

           
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    
                    if (!team.isActive) {
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

                
                Surface(
                    color = getDepartmentColor(team.department).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = team.department.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = getDepartmentColor(team.department),
                        fontWeight = FontWeight.Medium
                    )
                }

                if (team.description.isNotEmpty()) {
                    Text(
                        text = team.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${team.getTotalMembers()} membro(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${team.getTotalProjects()} projeto(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun getDepartmentColor(department: TeamDepartment): androidx.compose.ui.graphics.Color {
    return when (department) {
        TeamDepartment.ARCHITECTURE -> MaterialTheme.colorScheme.primary
        TeamDepartment.STRUCTURE -> MaterialTheme.colorScheme.secondary
        TeamDepartment.HYDRAULIC -> androidx.compose.ui.graphics.Color(0xFF2196F3)
        TeamDepartment.ELECTRICAL -> androidx.compose.ui.graphics.Color(0xFFFFC107)
        TeamDepartment.MASONRY -> androidx.compose.ui.graphics.Color(0xFF795548)
        TeamDepartment.FINISHING -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
        TeamDepartment.CLEANING -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        TeamDepartment.SAFETY -> androidx.compose.ui.graphics.Color(0xFFF44336)
        TeamDepartment.ADMINISTRATION -> MaterialTheme.colorScheme.tertiary
        TeamDepartment.PURCHASING -> androidx.compose.ui.graphics.Color(0xFF00BCD4)
        TeamDepartment.QUALITY -> androidx.compose.ui.graphics.Color(0xFF3F51B5)
        TeamDepartment.PLANNING -> androidx.compose.ui.graphics.Color(0xFFFF5722)
    }
}

private fun getDepartmentInitials(department: TeamDepartment): String {
    return when (department) {
        TeamDepartment.ARCHITECTURE -> "AR"
        TeamDepartment.STRUCTURE -> "ES"
        TeamDepartment.HYDRAULIC -> "HI"
        TeamDepartment.ELECTRICAL -> "EL"
        TeamDepartment.MASONRY -> "AL"
        TeamDepartment.FINISHING -> "AC"
        TeamDepartment.CLEANING -> "LI"
        TeamDepartment.SAFETY -> "SE"
        TeamDepartment.ADMINISTRATION -> "AD"
        TeamDepartment.PURCHASING -> "CO"
        TeamDepartment.QUALITY -> "QU"
        TeamDepartment.PLANNING -> "PL"
    }
}
