package com.project.taskmanagercivil.domain.models

/**
 * Permissões do sistema mapeadas por papel
 */
enum class Permission {
    // USUÁRIOS
    GERENCIAR_USUARIOS,           // Criar, editar, desativar usuários
    ATRIBUIR_PAPEIS,              // Atribuir papéis aos usuários

    // PROJETOS/OBRAS
    CRIAR_PROJETO,
    EDITAR_PROJETO_PROPRIO,       // Editar projetos que gerencia
    EDITAR_QUALQUER_PROJETO,      // Editar qualquer projeto
    APAGAR_PROJETO_PROPRIO,
    APAGAR_QUALQUER_PROJETO,
    VER_PROJETOS_PROPRIOS,        // Ver apenas projetos que gerencia
    VER_QUALQUER_PROJETO,         // Ver todos os projetos (read-only)

    // TAREFAS
    CRIAR_TAREFA,
    EDITAR_TAREFA_PROPRIA,        // Editar tarefas da sua equipe
    EDITAR_QUALQUER_TAREFA,
    EDITAR_STATUS_TAREFA_PROPRIA, // Funcionário edita status das suas tarefas
    APAGAR_TAREFA,
    INATIVAR_TAREFA,              // Líder pode inativar tarefas
    ATRIBUIR_TAREFA,
    CONCLUIR_TAREFA,
    APROVAR_ENTREGA,              // Líder/Gestor aprova entregas parciais
    SOLICITAR_REVISAO,            // Líder solicita revisão ao funcionário
    MARCAR_PRONTA_REVISAO,        // Funcionário marca como pronta para revisão
    VER_TAREFAS_PROPRIAS,         // Ver apenas suas tarefas
    VER_TAREFAS_EQUIPE,           // Ver tarefas da sua equipe
    VER_QUALQUER_TAREFA,          // Ver todas as tarefas (read-only)

    // EQUIPES
    CRIAR_EQUIPE,
    EDITAR_EQUIPE_PROPRIA,
    EDITAR_QUALQUER_EQUIPE,
    APAGAR_EQUIPE,
    ALOCAR_EQUIPE_PROJETO,

    // FUNCIONÁRIOS
    ADICIONAR_FUNCIONARIO,
    EDITAR_FUNCIONARIO,
    REMOVER_FUNCIONARIO,
    ADICIONAR_FUNCIONARIO_EQUIPE, // Líder adiciona à sua equipe

    // RELATÓRIOS
    VER_RELATORIOS_GLOBAIS,
    VER_RELATORIOS_PROJETOS_PROPRIOS,
    VER_RELATORIOS_EQUIPE,
    VER_RELATORIOS_TAREFAS_PROPRIAS,
    EXPORTAR_DADOS,

    // FINANCEIRO
    VER_CUSTOS,
    EDITAR_ORCAMENTOS,

    // SISTEMA
    ACESSAR_CONFIGURACOES,
    GERENCIAR_SISTEMA;

    companion object {
        /**
         * Retorna todas as permissões para um papel específico
         */
        fun forRole(role: Role): Set<Permission> {
            return when (role) {
                Role.ADMIN -> setOf(
                    // Pode tudo
                    GERENCIAR_USUARIOS,
                    ATRIBUIR_PAPEIS,
                    CRIAR_PROJETO,
                    EDITAR_QUALQUER_PROJETO,
                    APAGAR_QUALQUER_PROJETO,
                    VER_QUALQUER_PROJETO,
                    CRIAR_TAREFA,
                    EDITAR_QUALQUER_TAREFA,
                    APAGAR_TAREFA,
                    INATIVAR_TAREFA,
                    ATRIBUIR_TAREFA,
                    CONCLUIR_TAREFA,
                    APROVAR_ENTREGA,
                    SOLICITAR_REVISAO,
                    VER_QUALQUER_TAREFA,
                    CRIAR_EQUIPE,
                    EDITAR_QUALQUER_EQUIPE,
                    APAGAR_EQUIPE,
                    ALOCAR_EQUIPE_PROJETO,
                    ADICIONAR_FUNCIONARIO,
                    EDITAR_FUNCIONARIO,
                    REMOVER_FUNCIONARIO,
                    VER_RELATORIOS_GLOBAIS,
                    EXPORTAR_DADOS,
                    VER_CUSTOS,
                    EDITAR_ORCAMENTOS,
                    ACESSAR_CONFIGURACOES,
                    GERENCIAR_SISTEMA
                )

                Role.GESTOR_OBRAS -> setOf(
                    CRIAR_PROJETO,
                    EDITAR_PROJETO_PROPRIO,
                    APAGAR_PROJETO_PROPRIO,
                    VER_PROJETOS_PROPRIOS,
                    VER_QUALQUER_PROJETO, // Pode ver projetos de outros, mas não editar
                    CRIAR_TAREFA,
                    EDITAR_TAREFA_PROPRIA,
                    ATRIBUIR_TAREFA,
                    CONCLUIR_TAREFA,
                    APROVAR_ENTREGA,
                    VER_QUALQUER_TAREFA, // Pode ver tarefas de outras equipes
                    CRIAR_EQUIPE,
                    EDITAR_EQUIPE_PROPRIA,
                    ALOCAR_EQUIPE_PROJETO,
                    ADICIONAR_FUNCIONARIO,
                    EDITAR_FUNCIONARIO,
                    VER_RELATORIOS_PROJETOS_PROPRIOS,
                    EXPORTAR_DADOS,
                    VER_CUSTOS,
                    EDITAR_ORCAMENTOS
                )

                Role.LIDER_EQUIPE -> setOf(
                    VER_TAREFAS_EQUIPE,
                    VER_QUALQUER_PROJETO, // Pode ver projetos
                    VER_QUALQUER_TAREFA,  // Pode ver tarefas de outras equipes
                    CRIAR_TAREFA,
                    EDITAR_TAREFA_PROPRIA,
                    INATIVAR_TAREFA, // Pode inativar mas não apagar
                    ATRIBUIR_TAREFA,
                    CONCLUIR_TAREFA,
                    APROVAR_ENTREGA,
                    SOLICITAR_REVISAO,
                    EDITAR_EQUIPE_PROPRIA,
                    ADICIONAR_FUNCIONARIO_EQUIPE,
                    VER_RELATORIOS_EQUIPE
                )

                Role.FUNCIONARIO -> setOf(
                    VER_TAREFAS_PROPRIAS,
                    VER_QUALQUER_PROJETO, // Pode ver projetos
                    VER_QUALQUER_TAREFA,  // Pode ver tarefas de outras equipes
                    EDITAR_STATUS_TAREFA_PROPRIA,
                    MARCAR_PRONTA_REVISAO,
                    VER_RELATORIOS_TAREFAS_PROPRIAS
                )
            }
        }

        /**
         * Verifica se um conjunto de papéis tem uma permissão específica
         */
        fun hasPermission(roles: List<Role>, permission: Permission): Boolean {
            return roles.any { role ->
                forRole(role).contains(permission)
            }
        }
    }
}
