package com.weather.domain.model

import kotlinx.serialization.Serializable

/**
 * Sugestão de cidade retornada pelo endpoint de geocoding.
 *
 * Usada na [SearchSheet] e no [SearchViewModel] para exibir resultados
 * de busca e selecionar uma localidade para carregar a previsão.
 */
@Serializable
data class CidadeSugestao(
    val nome: String,
    /** Estado/província (admin1) — null para países sem divisão administrativa. */
    val estado: String?,
    val pais: String,
    val latitude: Double,
    val longitude: Double,
    val fusoHorario: String?
)
