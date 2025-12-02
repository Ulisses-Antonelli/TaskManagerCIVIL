package com.project.taskmanagercivil.data.remote.api

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.dto.financial.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Serviço de API para operações financeiras
 *
 * Endpoints do backend Spring Boot:
 * - GET /api/financial/tasks - Lista tarefas financeiras (com filtros)
 * - GET /api/financial/project/{projectId} - Dados financeiros de um projeto
 * - GET /api/financial/company - Dados financeiros da empresa
 * - PUT /api/financial/tasks/{taskId} - Atualiza dados financeiros de uma tarefa
 *
 * ⚠️ SEGURANÇA CRÍTICA: Dados financeiros são ALTAMENTE sensíveis
 * O backend DEVE validar permissões rigorosamente:
 * - ADMIN: Acesso total a todos os dados financeiros
 * - GESTOR_OBRAS: Vê dados de projetos que gerencia
 * - LIDER_EQUIPE: Vê dados resumidos (sem detalhes de custos/lucros)
 * - FUNCIONARIO: NÃO deve ter acesso (retornar 403 Forbidden)
 *
 * ⚠️ IMPORTANTE: Todas as requisições usam JWT token automaticamente via ApiClient
 * O backend valida permissões baseado no token JWT
 */
class FinancialApiService {

    /**
     * Lista tarefas financeiras com filtros opcionais
     *
     * Endpoint: GET /api/financial/tasks
     * Permissão necessária: VER_DADOS_FINANCEIROS
     * Roles permitidos: ADMIN, GESTOR_OBRAS
     *
     * Parâmetros de query suportados:
     * - projectId: Filtrar por projeto
     * - disciplineId: Filtrar por disciplina
     * - responsibleId: Filtrar por responsável
     * - status: Filtrar por status
     * - period: Filtrar por período (ex: "current_month", "last_quarter")
     *
     * @param projectId ID do projeto (opcional)
     * @param disciplineId ID da disciplina (opcional)
     * @param responsibleId ID do responsável (opcional)
     * @param status Status da tarefa (opcional)
     * @param period Período para filtrar (opcional)
     * @return Lista de FinancialTaskDto filtradas
     * @throws Exception se sem permissão (403) ou erro
     */
    suspend fun getFinancialTasks(
        projectId: String? = null,
        disciplineId: String? = null,
        responsibleId: String? = null,
        status: String? = null,
        period: String? = null
    ): List<FinancialTaskDto> {
        val response = ApiClient.httpClient.get("/financial/tasks") {
            url {
                projectId?.let { parameters.append("projectId", it) }
                disciplineId?.let { parameters.append("disciplineId", it) }
                responsibleId?.let { parameters.append("responsibleId", it) }
                status?.let { if (it != "Todos") parameters.append("status", it) }
                period?.let { parameters.append("period", it) }
            }
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para visualizar dados financeiros")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar tarefas financeiras: ${response.status}")
        }
    }

    /**
     * Busca dados financeiros detalhados de um projeto
     *
     * Endpoint: GET /api/financial/project/{projectId}
     * Permissão necessária: VER_DADOS_FINANCEIROS_PROJETO
     * Roles permitidos: ADMIN, GESTOR_OBRAS (apenas projetos que gerencia)
     *
     * Retorna análise financeira completa do projeto:
     * - KPIs (progresso físico/financeiro, eficiência, retrabalho)
     * - Valores contratuais e custos
     * - Distribuição por disciplina
     * - Análise de revisões
     *
     * @param projectId ID do projeto
     * @return ProjectFinancialsDto com dados completos
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun getProjectFinancials(projectId: String): ProjectFinancialsDto {
        val response = ApiClient.httpClient.get("/financial/project/$projectId")

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Projeto não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para visualizar dados financeiros deste projeto")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar dados financeiros do projeto: ${response.status}")
        }
    }

    /**
     * Busca dados financeiros da empresa
     *
     * Endpoint: GET /api/financial/company?period={period}
     * Permissão necessária: VER_DADOS_FINANCEIROS_EMPRESA
     * Roles permitidos: ADMIN apenas
     *
     * Retorna visão geral financeira da empresa:
     * - Receita, custos, lucro líquido
     * - Projetos ativos e concluídos
     * - Ranking de projetos por lucratividade
     * - Análise de fluxo de caixa
     * - Indicadores complementares
     *
     * @param period Período para análise ("current_month", "last_quarter", "year_to_date")
     * @return CompanyFinancialsDto com dados consolidados
     * @throws Exception se sem permissão (403) ou erro
     */
    suspend fun getCompanyFinancials(period: String = "current_month"): CompanyFinancialsDto {
        val response = ApiClient.httpClient.get("/financial/company") {
            url {
                parameters.append("period", period)
            }
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para visualizar dados financeiros da empresa")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar dados financeiros da empresa: ${response.status}")
        }
    }

    /**
     * Atualiza dados financeiros de uma tarefa
     *
     * Endpoint: PUT /api/financial/tasks/{taskId}
     * Permissão necessária: EDITAR_DADOS_FINANCEIROS_TAREFA
     * Roles permitidos: ADMIN, GESTOR_OBRAS (para tarefas de seus projetos)
     *
     * Permite atualizar dias reais e custo real de uma tarefa concluída.
     * O backend deve recalcular automaticamente:
     * - profitLoss (estimatedCost - actualCost)
     * - Métricas agregadas do projeto
     * - Indicadores da empresa
     *
     * @param taskId ID da tarefa
     * @param actualDays Dias reais gastos
     * @param actualCost Custo real incorrido
     * @return FinancialTaskDto atualizado
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun updateTaskFinancials(
        taskId: String,
        actualDays: Int,
        actualCost: Double
    ): FinancialTaskDto {
        val updateDto = UpdateFinancialTaskDto(
            actualDays = actualDays,
            actualCost = actualCost
        )

        val response = ApiClient.httpClient.put("/financial/tasks/$taskId") {
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Tarefa não encontrada")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para editar dados financeiros desta tarefa")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao atualizar dados financeiros da tarefa: ${response.status}")
        }
    }
}
