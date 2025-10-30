package com.project.taskmanagercivil.domain.models

/**
 * Helper class para verificar permissões de forma conveniente
 * Centraliza toda a lógica de autorização do sistema
 */
object PermissionChecker {

    /**
     * Verifica se o usuário tem permissão específica
     */
    fun hasPermission(user: User?, permission: Permission): Boolean {
        return user?.hasPermission(permission) ?: false
    }

    /**
     * Verifica se o usuário é admin
     */
    fun isAdmin(user: User?): Boolean {
        return user?.isAdmin ?: false
    }

    /**
     * Verifica se o usuário tem um dos papéis especificados
     */
    fun hasAnyRole(user: User?, vararg roles: Role): Boolean {
        return user?.roles?.any { it in roles } ?: false
    }

    // === PERMISSÕES DE PROJETOS ===

    fun canCreateProject(user: User?): Boolean {
        return hasPermission(user, Permission.CRIAR_PROJETO)
    }

    fun canEditProject(user: User?, projectId: String): Boolean {
        // Admin pode editar qualquer projeto
        if (hasPermission(user, Permission.EDITAR_QUALQUER_PROJETO)) return true

        // Gestor pode editar seus próprios projetos
        if (hasPermission(user, Permission.EDITAR_PROJETO_PROPRIO)) {
            // Aqui você verificaria se o projeto pertence ao gestor
            // Por enquanto, retorna true se tem a permissão
            return true
        }

        return false
    }

    fun canDeleteProject(user: User?, projectId: String): Boolean {
        // Admin pode apagar qualquer projeto
        if (hasPermission(user, Permission.APAGAR_QUALQUER_PROJETO)) return true

        // Gestor pode apagar seus próprios projetos
        if (hasPermission(user, Permission.APAGAR_PROJETO_PROPRIO)) {
            // Aqui você verificaria se o projeto pertence ao gestor
            return true
        }

        return false
    }

    fun canViewProject(user: User?, projectId: String): Boolean {
        return hasPermission(user, Permission.VER_QUALQUER_PROJETO) ||
               hasPermission(user, Permission.VER_PROJETOS_PROPRIOS)
    }

    // === PERMISSÕES DE TAREFAS ===

    fun canCreateTask(user: User?): Boolean {
        return hasPermission(user, Permission.CRIAR_TAREFA)
    }

    fun canEditTask(user: User?, taskId: String): Boolean {
        // Admin pode editar qualquer tarefa
        if (hasPermission(user, Permission.EDITAR_QUALQUER_TAREFA)) return true

        // Gestor/Líder pode editar tarefas da sua equipe
        if (hasPermission(user, Permission.EDITAR_TAREFA_PROPRIA)) {
            // Verificaria se a tarefa pertence à equipe do usuário
            return true
        }

        // Funcionário pode editar status das suas tarefas
        if (hasPermission(user, Permission.EDITAR_STATUS_TAREFA_PROPRIA)) {
            // Verificaria se a tarefa está atribuída ao funcionário
            return true
        }

        return false
    }

    fun canDeleteTask(user: User?): Boolean {
        return hasPermission(user, Permission.APAGAR_TAREFA)
    }

    fun canInactivateTask(user: User?): Boolean {
        return hasPermission(user, Permission.INATIVAR_TAREFA)
    }

    fun canAssignTask(user: User?): Boolean {
        return hasPermission(user, Permission.ATRIBUIR_TAREFA)
    }

    fun canCompleteTask(user: User?): Boolean {
        return hasPermission(user, Permission.CONCLUIR_TAREFA)
    }

    fun canApproveDelivery(user: User?): Boolean {
        return hasPermission(user, Permission.APROVAR_ENTREGA)
    }

    fun canRequestRevision(user: User?): Boolean {
        return hasPermission(user, Permission.SOLICITAR_REVISAO)
    }

    fun canMarkReadyForReview(user: User?): Boolean {
        return hasPermission(user, Permission.MARCAR_PRONTA_REVISAO)
    }

    fun canViewTask(user: User?, taskId: String): Boolean {
        return hasPermission(user, Permission.VER_QUALQUER_TAREFA) ||
               hasPermission(user, Permission.VER_TAREFAS_EQUIPE) ||
               hasPermission(user, Permission.VER_TAREFAS_PROPRIAS)
    }

    // === PERMISSÕES DE EQUIPES ===

    fun canCreateTeam(user: User?): Boolean {
        return hasPermission(user, Permission.CRIAR_EQUIPE)
    }

    fun canEditTeam(user: User?, teamId: String): Boolean {
        // Admin pode editar qualquer equipe
        if (hasPermission(user, Permission.EDITAR_QUALQUER_EQUIPE)) return true

        // Gestor/Líder pode editar sua própria equipe
        if (hasPermission(user, Permission.EDITAR_EQUIPE_PROPRIA)) {
            // Verificaria se é a equipe do usuário
            return true
        }

        return false
    }

    fun canDeleteTeam(user: User?): Boolean {
        return hasPermission(user, Permission.APAGAR_EQUIPE)
    }

    fun canAllocateTeam(user: User?): Boolean {
        return hasPermission(user, Permission.ALOCAR_EQUIPE_PROJETO)
    }

    // === PERMISSÕES DE FUNCIONÁRIOS ===

    fun canAddEmployee(user: User?): Boolean {
        return hasPermission(user, Permission.ADICIONAR_FUNCIONARIO) ||
               hasPermission(user, Permission.ADICIONAR_FUNCIONARIO_EQUIPE)
    }

    fun canEditEmployee(user: User?): Boolean {
        return hasPermission(user, Permission.EDITAR_FUNCIONARIO)
    }

    fun canRemoveEmployee(user: User?): Boolean {
        return hasPermission(user, Permission.REMOVER_FUNCIONARIO)
    }

    // === PERMISSÕES DE RELATÓRIOS ===

    fun canViewGlobalReports(user: User?): Boolean {
        return hasPermission(user, Permission.VER_RELATORIOS_GLOBAIS)
    }

    fun canViewProjectReports(user: User?): Boolean {
        return hasPermission(user, Permission.VER_RELATORIOS_PROJETOS_PROPRIOS) ||
               hasPermission(user, Permission.VER_RELATORIOS_GLOBAIS)
    }

    fun canViewTeamReports(user: User?): Boolean {
        return hasPermission(user, Permission.VER_RELATORIOS_EQUIPE) ||
               hasPermission(user, Permission.VER_RELATORIOS_GLOBAIS)
    }

    fun canExportData(user: User?): Boolean {
        return hasPermission(user, Permission.EXPORTAR_DADOS)
    }

    // === PERMISSÕES FINANCEIRAS ===

    fun canViewCosts(user: User?): Boolean {
        return hasPermission(user, Permission.VER_CUSTOS)
    }

    fun canEditBudgets(user: User?): Boolean {
        return hasPermission(user, Permission.EDITAR_ORCAMENTOS)
    }

    // === PERMISSÕES DE SISTEMA ===

    fun canManageUsers(user: User?): Boolean {
        return hasPermission(user, Permission.GERENCIAR_USUARIOS)
    }

    fun canAssignRoles(user: User?): Boolean {
        return hasPermission(user, Permission.ATRIBUIR_PAPEIS)
    }

    fun canAccessSettings(user: User?): Boolean {
        return hasPermission(user, Permission.ACESSAR_CONFIGURACOES)
    }

    fun canManageSystem(user: User?): Boolean {
        return hasPermission(user, Permission.GERENCIAR_SISTEMA)
    }

    // === HELPERS PARA UI ===

    /**
     * Retorna true se o botão de criar projeto deve ser visível
     */
    fun shouldShowCreateProjectButton(user: User?): Boolean {
        return canCreateProject(user)
    }

    /**
     * Retorna true se o botão de editar projeto deve ser visível
     */
    fun shouldShowEditProjectButton(user: User?, projectId: String): Boolean {
        return canEditProject(user, projectId)
    }

    /**
     * Retorna true se o botão de apagar projeto deve ser visível
     */
    fun shouldShowDeleteProjectButton(user: User?, projectId: String): Boolean {
        return canDeleteProject(user, projectId)
    }

    /**
     * Retorna true se o menu de configurações deve ser visível
     */
    fun shouldShowSettingsMenu(user: User?): Boolean {
        return canAccessSettings(user)
    }

    /**
     * Retorna mensagem de erro para ação não permitida
     */
    fun getUnauthorizedMessage(user: User?, action: String): String {
        return if (user == null) {
            "Você precisa estar logado para $action"
        } else {
            "Você não tem permissão para $action. Papel atual: ${user.primaryRoleDisplayName}"
        }
    }
}
