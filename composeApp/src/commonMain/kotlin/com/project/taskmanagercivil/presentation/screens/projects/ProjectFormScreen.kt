package com.project.taskmanagercivil.presentation.screens.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    viewModel: ProjectFormViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(1000)
            viewModel.clearMessages()
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.projectId != null) "Editar Projeto" else "Novo Projeto"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    ProjectFormContent(
                        uiState = uiState,
                        onNameChange = viewModel::onNameChange,
                        onDescriptionChange = viewModel::onDescriptionChange,
                        onClientChange = viewModel::onClientChange,
                        onLocationChange = viewModel::onLocationChange,
                        onStartDateChange = viewModel::onStartDateChange,
                        onEndDateChange = viewModel::onEndDateChange,
                        onBudgetChange = viewModel::onBudgetChange,
                        onSave = { viewModel.saveProject(onSaveSuccess) }
                    )
                }
            }

            // Snackbar para mensagens
            if (uiState.error != null || uiState.successMessage != null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.error != null)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = uiState.error ?: uiState.successMessage ?: "",
                        modifier = Modifier.padding(16.dp),
                        color = if (uiState.error != null)
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectFormContent(
    uiState: ProjectFormUiState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onClientChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onBudgetChange: (String) -> Unit,
    onSave: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Seção: Informações Básicas
        item {
            Text(
                text = "Informações Básicas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text("Nome do Projeto *") },
                leadingIcon = {
                    Icon(Icons.Default.Build, contentDescription = null)
                },
                isError = uiState.nameError != null,
                supportingText = uiState.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = { Text("Descrição *") },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                },
                isError = uiState.descriptionError != null,
                supportingText = uiState.descriptionError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }

        // Seção: Cliente e Localização
        item {
            Text(
                text = "Cliente e Localização",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = uiState.client,
                onValueChange = onClientChange,
                label = { Text("Cliente *") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                isError = uiState.clientError != null,
                supportingText = uiState.clientError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = uiState.location,
                onValueChange = onLocationChange,
                label = { Text("Localização *") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                },
                placeholder = { Text("Ex: São Paulo - SP") },
                isError = uiState.locationError != null,
                supportingText = uiState.locationError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Seção: Cronograma e Orçamento
        item {
            Text(
                text = "Cronograma e Orçamento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.startDate,
                    onValueChange = onStartDateChange,
                    label = { Text("Data Início *") },
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.endDate,
                    onValueChange = onEndDateChange,
                    label = { Text("Data Fim *") },
                    leadingIcon = {
                        Icon(Icons.Default.Event, contentDescription = null)
                    },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        if (uiState.dateError != null) {
            item {
                Text(
                    text = uiState.dateError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        item {
            OutlinedTextField(
                value = uiState.budget,
                onValueChange = onBudgetChange,
                label = { Text("Orçamento (R$) *") },
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, contentDescription = null)
                },
                placeholder = { Text("Ex: 15000000") },
                isError = uiState.budgetError != null,
                supportingText = uiState.budgetError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Botão Salvar
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (uiState.isSaving) "Salvando..." else "Salvar Projeto",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        item {
            Text(
                text = "* Campos obrigatórios",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
