package com.weather.domain.repository

import com.weather.domain.model.CidadeSugestao
import com.weather.domain.model.HistoricoBusca
import com.weather.utils.AppResult

interface IBuscaRepository {

    /** Busca cidades pelo nome via API de geocoding (máx. 5 resultados). */
    suspend fun buscarCidades(nome: String): AppResult<List<CidadeSugestao>>

    /**
     * Persiste a cidade selecionada no histórico local.
     *
     * Cidades duplicadas são movidas para o topo; lista limitada a 5 entradas.
     */
    suspend fun salvarNoBusca(cidade: CidadeSugestao)

    /** Retorna o histórico de cidades, da mais recente para a mais antiga (máx. 5). */
    suspend fun obterHistorico(): List<HistoricoBusca>
}
