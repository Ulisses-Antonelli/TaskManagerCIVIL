package com.project.taskmanagercivil.presentation.screens.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.*
import com.project.taskmanagercivil.utils.FormatUtils
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailScreen(
    viewModel: DocumentDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onProjectClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Documento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (uiState.document != null) {
                        IconButton(onClick = { onEdit(uiState.document!!.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = {
                            onDelete(uiState.document!!.id)
                            onBack()
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Deletar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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

                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refreshDocument() }) {
                            Text("Tentar novamente")
                        }
                    }
                }

                uiState.document != null -> {
                    DocumentDetailContent(
                        document = uiState.document!!,
                        project = uiState.project,
                        creator = uiState.creator,
                        versions = uiState.versions,
                        approvals = uiState.approvals,
                        onProjectClick = onProjectClick
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentDetailContent(
    document: Document,
    project: Project?,
    creator: Employee?,
    versions: List<DocumentVersion>,
    approvals: List<DocumentApproval>,
    onProjectClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(getCategoryColor(document.category)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getCategoryIcon(document.category),
                                style = MaterialTheme.typography.displaySmall
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = document.code,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = document.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                color = getStatusColor(document.status),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = document.status.displayName,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Informações Gerais") {
                InfoRow(label = "Tipo", value = document.type.displayName)
                InfoRow(label = "Categoria", value = document.category.displayName)
                if (document.discipline != null) {
                    InfoRow(label = "Disciplina", value = document.discipline.displayName)
                }
                InfoRow(label = "Fase", value = document.phase.displayName)
                InfoRow(label = "Revisão Atual", value = document.currentRevision)
                InfoRow(label = "Data de Criação", value = FormatUtils.formatDate(document.createdDate))
                if (creator != null) {
                    InfoRow(label = "Criado Por", value = creator.fullName)
                }
                if (document.fileSize != null) {
                    InfoRow(label = "Tamanho do Arquivo", value = formatFileSize(document.fileSize))
                }
            }
        }

        if (document.description != null && document.description.isNotBlank()) {
            item {
                SectionCard(title = "Descrição") {
                    Text(
                        text = document.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (document.tags.isNotEmpty()) {
            item {
                SectionCard(title = "Tags") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        document.tags.forEach { tag ->
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        if (project != null) {
            item {
                Text(
                    text = "Projeto Relacionado",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ProjectCard(
                    project = project,
                    onClick = { onProjectClick(project.id) }
                )
            }
        }

        if (versions.isNotEmpty()) {
            item {
                Text(
                    text = "Histórico de Versões (${versions.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(versions) { version ->
                VersionCard(version = version)
            }
        }

        if (approvals.isNotEmpty()) {
            item {
                Text(
                    text = "Fluxo de Aprovação",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ApprovalStageCard(
                            stage = ApprovalStage.DESIGNER,
                            approval = approvals.find { it.stage == ApprovalStage.DESIGNER },
                            modifier = Modifier.weight(1f)
                        )

                        ApprovalStageCard(
                            stage = ApprovalStage.COORDINATOR,
                            approval = approvals.find { it.stage == ApprovalStage.COORDINATOR },
                            modifier = Modifier.weight(1f)
                        )

                        ApprovalStageCard(
                            stage = ApprovalStage.MANAGER,
                            approval = approvals.find { it.stage == ApprovalStage.MANAGER },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (document.fileUrl != null) {
            item {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Visualizar Documento")
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = project.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cliente: ${project.client}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Prazo: ${FormatUtils.formatDate(project.endDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VersionCard(version: DocumentVersion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (version.isSuperseded)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = version.revision,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (version.isSuperseded) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Superado",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Text(
                    text = FormatUtils.formatDate(version.versionDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = getStatusColor(version.status),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = version.status.displayName,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ApprovalStageCard(
    stage: ApprovalStage,
    approval: DocumentApproval?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (approval?.status) {
                ApprovalStatus.APPROVED -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                ApprovalStatus.REJECTED -> Color(0xFFF44336).copy(alpha = 0.1f)
                ApprovalStatus.PENDING -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stage.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            if (approval != null) {
                Surface(
                    color = when (approval.status) {
                        ApprovalStatus.APPROVED -> Color(0xFF4CAF50)
                        ApprovalStatus.REJECTED -> Color(0xFFF44336)
                        ApprovalStatus.PENDING -> Color(0xFFFF9800)
                        ApprovalStatus.NOT_REQUIRED -> Color(0xFF9E9E9E)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = approval.status.displayName,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }

                Text(
                    text = approval.approverName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )

                if (approval.date != null) {
                    Text(
                        text = formatDateTime(approval.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (approval.comments != null && approval.comments.isNotBlank()) {
                    Text(
                        text = approval.comments,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3
                    )
                }
            } else {
                Text(
                    text = "Não configurado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun getCategoryColor(category: DocumentCategory): Color {
    return when (category) {
        DocumentCategory.PLANS_PROJECTS -> Color(0xFF2196F3)
        DocumentCategory.TECHNICAL -> Color(0xFF9C27B0)
        DocumentCategory.LEGAL_CONTRACTUAL -> Color(0xFF795548)
        DocumentCategory.FINANCIAL -> Color(0xFF4CAF50)
        DocumentCategory.QUALITY -> Color(0xFFFFC107)
        DocumentCategory.CONSTRUCTION_SITE -> Color(0xFFFF5722)
        DocumentCategory.OTHER -> Color(0xFF607D8B)
    }
}

private fun getCategoryIcon(category: DocumentCategory): String {
    return when (category) {
        DocumentCategory.PLANS_PROJECTS -> "📐"
        DocumentCategory.TECHNICAL -> "📋"
        DocumentCategory.LEGAL_CONTRACTUAL -> "📜"
        DocumentCategory.FINANCIAL -> "💰"
        DocumentCategory.QUALITY -> "✓"
        DocumentCategory.CONSTRUCTION_SITE -> "🏗"
        DocumentCategory.OTHER -> "📄"
    }
}

@Composable
private fun getStatusColor(status: DocumentStatus): Color {
    return when (status) {
        DocumentStatus.APPROVED -> Color(0xFF4CAF50)
        DocumentStatus.IN_PROGRESS -> Color(0xFF2196F3)
        DocumentStatus.FOR_REVIEW -> Color(0xFFFFC107)
        DocumentStatus.IN_APPROVAL -> Color(0xFFFF9800)
        DocumentStatus.REJECTED -> Color(0xFFF44336)
        DocumentStatus.SUPERSEDED -> Color(0xFF9E9E9E)
        DocumentStatus.ARCHIVED -> Color(0xFF757575)
    }
}

private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0

    return when {
        mb >= 1 -> {
            val rounded = (mb * 10).toLong() / 10.0
            "$rounded MB"
        }
        kb >= 1 -> {
            val rounded = (kb * 10).toLong() / 10.0
            "$rounded KB"
        }
        else -> "$bytes B"
    }
}

private fun formatDateTime(dateTime: LocalDateTime): String {
    val day = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.monthNumber.toString().padStart(2, '0')
    val year = dateTime.year
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "$day/$month/$year $hour:$minute"
}
