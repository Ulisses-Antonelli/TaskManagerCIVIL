package com.project.taskmanagercivil.data.remote.api

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.dto.dashboard.DashboardDto
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Serviço de API para operações com Dashboard
 *
 * Endpoint do backend Spring Boot:
 * - GET /api/dashboard - Retorna dados agregados do dashboard
 *
 * ⚠️ IMPORTANTE: Este endpoint faz TODOS os cálculos no backend
 * O backend deve:
 * 1. Calcular contagens de projetos por status
 * 2. Calcular estatísticas de progresso (total, concluídas, média, atrasadas)
 * 3. Identificar prazos críticos (próximos 7 dias ou atrasados)
 * 4. Calcular indicadores financeiros por projeto
 * 5. Gerar dados mensais para gráficos (últimos 6 meses)
 * 6. Filtrar dados por permissões do usuário autenticado
 *
 * ⚠️ PERMISSÕES: O backend deve retornar dados de acordo com o role do usuário:
 * - ADMIN: Todos os dados de todos os projetos
 * - GESTOR_OBRAS: Dados de projetos que gerencia
 * - LIDER_EQUIPE: Dados de projetos/tarefas da sua equipe
 * - FUNCIONARIO: Dados resumidos (sem detalhes financeiros)
 *
 * ⚠️ IMPORTANTE: Todas as requisições usam JWT token automaticamente via ApiClient
 * O backend valida permissões baseado no token JWT
 */
class DashboardApiService {

    /**
     * Busca dados agregados do dashboard
     *
     * Endpoint: GET /api/dashboard
     * Permissão: Todos podem visualizar (dados filtrados por role no backend)
     *
     * O backend retorna:
     * - projectStatusSummary: Contagem de projetos por status
     * - progressStats: Estatísticas de progresso (total, concluídas, média, atrasadas)
     * - criticalDeadlines: Lista de tarefas com prazo crítico (próximos 7 dias)
     * - financialIndicators: Indicadores financeiros por projeto (filtrado por permissão)
     * - monthlyProjectData: Dados mensais para gráficos (últimos 6 meses)
     *
     * @return DashboardDto com todos os dados calculados pelo backend
     * @throws Exception se houver erro de rede ou autenticação
     */
    suspend fun getDashboardData(): DashboardDto {
        val response = ApiClient.httpClient.get("/dashboard")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar dados do dashboard: ${response.status}")
        }
    }

    /**
     * Busca dados do dashboard para um período específico
     *
     * Endpoint: GET /api/dashboard?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd
     * Permissão: Todos podem visualizar (dados filtrados por role no backend)
     *
     * Permite filtrar dados por período personalizado
     *
     * @param startDate Data inicial no formato "yyyy-MM-dd" (ex: "2024-01-01")
     * @param endDate Data final no formato "yyyy-MM-dd" (ex: "2024-12-31")
     * @return DashboardDto com dados do período especificado
     * @throws Exception se houver erro
     */
    suspend fun getDashboardDataByPeriod(startDate: String, endDate: String): DashboardDto {
        val response = ApiClient.httpClient.get("/dashboard") {
            url {
                parameters.append("startDate", startDate)
                parameters.append("endDate", endDate)
            }
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar dados do dashboard por período: ${response.status}")
        }
    }

    /**
     * Busca dados do dashboard para um projeto específico
     *
     * Endpoint: GET /api/dashboard/project/{projectId}
     * Permissão: Depende do projeto (backend valida se usuário tem acesso)
     *
     * Retorna dashboard focado em um único projeto
     *
     * @param projectId ID do projeto
     * @return DashboardDto com dados específicos do projeto
     * @throws Exception se projeto não encontrado, sem permissão ou erro
     */
    suspend fun getDashboardDataByProject(projectId: String): DashboardDto {
        val response = ApiClient.httpClient.get("/dashboard/project/$projectId")

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Projeto não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para visualizar este projeto")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar dados do dashboard do projeto: ${response.status}")
        }
    }
}
