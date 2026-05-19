package com.weather.presentation.search

import com.weather.domain.model.CidadeSugestao
import com.weather.domain.model.HistoricoBusca

/**
 * Estados da UI do painel de busca de cidades.
 *
 * Emitido pelo [SearchViewModel] e consumido pelo [SearchSheet].
 */
sealed class SearchUiState {

    /** Nenhuma query digitada — exibe histórico completo (até 5 entradas). */
    data object Idle : SearchUiState()

    /** Requisição de geocoding em andamento. */
    data object Carregando : SearchUiState()

    /**
     * Resultados disponíveis.
     *
     * @param sugestoes lista de até 5 cidades da API de geocoding
     * @param historico lista de até 5 cidades do histórico local (exibida se [sugestoes] vazio)
     */
    data class Resultados(
        val sugestoes: List<CidadeSugestao>,
        val historico: List<HistoricoBusca>
    ) : SearchUiState()

    /** Falha na busca (ex: sem internet, erro de API). */
    data class Erro(val mensagem: String) : SearchUiState()
}
