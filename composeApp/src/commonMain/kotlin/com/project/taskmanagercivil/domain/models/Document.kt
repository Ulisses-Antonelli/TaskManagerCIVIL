package com.project.taskmanagercivil.domain.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Modelo de dados para Documento
 */
data class Document(
    val id: String,
    val code: String, // Código padronizado: OBRA-DISC-SEQ-REV (ex: VV-ARQ-001-R02)
    val title: String, // Título descritivo do documento
    val type: DocumentType, // Tipo de documento
    val category: DocumentCategory, // Categoria para organização
    val discipline: DocumentDiscipline?, // Disciplina (apenas para plantas/projetos)
    val taskId: String, // ID da tarefa relacionada (documentos pertencem a tarefas)
    val projectId: String, // ID do projeto/obra relacionado (herdado da tarefa)
    val phase: ProjectPhase, // Fase do projeto
    val status: DocumentStatus, // Status atual
    val currentRevision: String, // Revisão atual (R00, R01, etc)
    val createdDate: LocalDate, // Data de criação
    val createdBy: String, // ID do criador
    val fileUrl: String?, // URL do arquivo (PDF, DWG, etc)
    val fileSize: Long?, // Tamanho do arquivo em bytes
    val tags: List<String> = emptyList(), // Tags para busca
    val description: String? = null, // Descrição adicional
    val isSuperseded: Boolean = false // Se foi superado por nova versão
)

/**
 * Histórico de versões do documento
 */
data class DocumentVersion(
    val id: String,
    val documentId: String,
    val revision: String, // R00, R01, R02...
    val versionDate: LocalDate,
    val createdBy: String, // ID do criador da versão
    val changes: String, // Descrição das alterações
    val fileUrl: String?,
    val status: DocumentStatus,
    val isSuperseded: Boolean = false // Se foi superado
)

/**
 * Aprovação de documento
 */
data class DocumentApproval(
    val id: String,
    val documentId: String,
    val revision: String,
    val stage: ApprovalStage, // Projetista, Coordenador, Gerente
    val approver: String, // ID do aprovador
    val approverName: String, // Nome do aprovador
    val approverRole: String, // Cargo do aprovador
    val status: ApprovalStatus, // Pendente, Aprovado, Reprovado
    val date: LocalDateTime?,
    val comments: String? = null // Observações
)

/**
 * Tipo de documento
 */
enum class DocumentType(val displayName: String) {
    // Plantas e Projetos
    ARCHITECTURAL_PLAN("Planta Arquitetônica"),
    STRUCTURAL_PLAN("Planta Estrutural"),
    HYDRAULIC_PLAN("Planta Hidráulica"),
    ELECTRICAL_PLAN("Planta Elétrica"),
    FOUNDATION_PLAN("Planta de Fundações"),

    // Documentos Técnicos
    CALCULATION_MEMORY("Memória de Cálculo"),
    TECHNICAL_SPECIFICATION("Especificação Técnica"),
    INSPECTION_REPORT("Relatório de Inspeção"),
    TECHNICAL_REPORT("Laudo Técnico"),

    // Documentos Legais/Contratuais
    CONTRACT("Contrato"),
    LICENSE("Alvará/Licença"),
    ADDENDUM("Aditivo Contratual"),

    // Documentos Financeiros
    MEASUREMENT("Medição"),
    INVOICE("Nota Fiscal"),
    PAYMENT_REQUEST("Solicitação de Pagamento"),

    // Documentos de Qualidade
    SERVICE_VERIFICATION("Ficha de Verificação de Serviço (FVS)"),
    CHECKLIST("Checklist"),
    QUALITY_REPORT("Relatório de Qualidade"),

    // Documentos da Obra
    DAILY_REPORT("Diário de Obra (RDO)"),
    PHOTO_RECORD("Registro Fotográfico"),
    MEETING_MINUTES("Ata de Reunião"),

    // Outros
    OTHER("Outro")
}

/**
 * Categoria para organização hierárquica
 */
enum class DocumentCategory(val displayName: String) {
    PLANS_PROJECTS("Plantas e Projetos"),
    TECHNICAL("Documentos Técnicos"),
    LEGAL_CONTRACTUAL("Documentos Legais/Contratuais"),
    FINANCIAL("Documentos Financeiros"),
    QUALITY("Documentos de Qualidade"),
    CONSTRUCTION_SITE("Documentos da Obra"),
    OTHER("Outros")
}

/**
 * Disciplina (para plantas/projetos)
 */
enum class DocumentDiscipline(val displayName: String, val code: String) {
    ARCHITECTURE("Arquitetura", "ARQ"),
    STRUCTURE("Estrutura", "EST"),
    FOUNDATION("Fundações", "FUN"),
    HYDRAULIC("Hidráulica", "HID"),
    ELECTRICAL("Elétrica", "ELE"),
    HVAC("Climatização", "CLI"),
    FIRE("Prevenção de Incêndio", "PCI"),
    LANDSCAPING("Paisagismo", "PAI"),
    TOPOGRAPHY("Topografia", "TOP")
}

/**
 * Fase do projeto
 */
enum class ProjectPhase(val displayName: String) {
    PRELIMINARY_STUDY("Estudo Preliminar"),
    BASIC_PROJECT("Projeto Básico"),
    EXECUTIVE_PROJECT("Projeto Executivo"),
    AS_BUILT("As-Built"),
    CONSTRUCTION("Execução"),
    CLOSING("Encerramento")
}

/**
 * Status do documento
 */
enum class DocumentStatus(val displayName: String) {
    IN_PROGRESS("Em Elaboração"),
    FOR_REVIEW("Para Revisão"),
    IN_APPROVAL("Em Aprovação"),
    APPROVED("Aprovado"),
    REJECTED("Reprovado"),
    SUPERSEDED("Superado"),
    ARCHIVED("Arquivado")
}

/**
 * Estágio de aprovação
 */
enum class ApprovalStage(val displayName: String, val order: Int) {
    DESIGNER("Projetista", 1),
    COORDINATOR("Coordenador", 2),
    MANAGER("Gerente", 3)
}

/**
 * Status de aprovação
 */
enum class ApprovalStatus(val displayName: String) {
    PENDING("Pendente"),
    APPROVED("Aprovado"),
    REJECTED("Reprovado"),
    NOT_REQUIRED("Não Requerido")
}
