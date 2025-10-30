package com.project.taskmanagercivil.domain.models

/**
 * Papéis/Funções no sistema
 * Um usuário pode ter múltiplos papéis
 */
enum class Role(val displayName: String, val description: String) {
    ADMIN(
        displayName = "Administrador",
        description = "Acesso total ao sistema, pode gerenciar tudo"
    ),
    GESTOR_OBRAS(
        displayName = "Gestor de Obras",
        description = "Gerencia projetos/obras, equipes e orçamentos"
    ),
    LIDER_EQUIPE(
        displayName = "Líder de Equipe",
        description = "Gerencia tarefas e membros da sua equipe"
    ),
    FUNCIONARIO(
        displayName = "Funcionário",
        description = "Executa e atualiza tarefas atribuídas"
    );

    companion object {
        /**
         * Converte string para Role, retorna FUNCIONARIO como padrão
         */
        fun fromString(value: String): Role {
            return entries.find {
                it.name.equals(value, ignoreCase = true) ||
                it.displayName.equals(value, ignoreCase = true)
            } ?: FUNCIONARIO
        }
    }
}
