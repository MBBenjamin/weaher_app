package com.weather.presentation.home

import com.weather.domain.model.Previsao

/**
 * Estados da UI da tela principal.
 *
 * Emitido pelo [HomeViewModel] e consumido pelo [HomeScreen] para decidir
 * qual composable exibir a cada instante.
 */
sealed class HomeUiState {

    /** Carregando dados pela primeira vez (exibe [LoadingSkeleton]). */
    data object Carregando : HomeUiState()

    /**
     * Dados disponíveis para exibição.
     *
     * @param previsao dados meteorológicos completos
     * @param nomeLocalidade ex: "São Paulo, SP"
     * @param isLocalizacaoAproximada `true` enquanto GPS ainda não refinou a posição
     * @param isOffline `true` quando o dispositivo está sem internet
     * @param timestampRelativo ex: "Atualizado há 5 min"
     * @param horasAtraso horas desde a última atualização — exibido no [OfflineBadge]
     */
    data class Sucesso(
        val previsao: Previsao,
        val nomeLocalidade: String,
        val isLocalizacaoAproximada: Boolean = false,
        val isOffline: Boolean = false,
        val timestampRelativo: String = "",
        val horasAtraso: Int = 0
    ) : HomeUiState()

    /**
     * Falha no carregamento.
     *
     * @param mensagem mensagem de erro em PT-BR
     * @param temCache `true` se há dados em cache (mesmo que expirados)
     * @param previsaoCache previsão em cache disponível para exibição degradada
     * @param rateLimitSecondsRemaining segundos restantes do countdown de rate limit (T019)
     */
    data class Erro(
        val mensagem: String,
        val temCache: Boolean = false,
        val previsaoCache: Previsao? = null,
        val rateLimitSecondsRemaining: Int? = null
    ) : HomeUiState()

    /** Permissão de localização negada (exibe botão para abrir configurações). */
    data object SemPermissao : HomeUiState()

    /** Permissão negada ≥3 vezes — exibe botão "Abrir configurações" (CE-04). */
    data object SemPermissaoDefinitiva : HomeUiState()
}
