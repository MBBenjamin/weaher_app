package com.weather.domain.model

/**
 * Entrada do histórico de cidades buscadas pelo usuário.
 *
 * Exibida na [SearchSheet] ao focar o campo de busca (antes de digitar).
 * Máximo 5 entradas, ordenadas da mais recente para a mais antiga.
 */
data class HistoricoBusca(
    val nomeCidade: String,
    /** Estado/província — null quando não disponível na API de geocoding. */
    val estado: String?,
    val pais: String,
    val latitude: Double,
    val longitude: Double,
    /** Epoch millis da última seleção desta cidade pelo usuário. */
    val buscadoEm: Long
)
