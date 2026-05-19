package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para cada cidade retornada pelo endpoint `/v1/search` da Open-Meteo Geocoding API.
 *
 * Campos opcionais (`admin1`, `population`, `timezone`, `elevation`) podem estar ausentes
 * dependendo da cobertura de dados da região. Mapeado para [CidadeSugestao] via mapper.
 */
@Serializable
data class CidadeDto(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null,
    @SerialName("country_code") val countryCode: String,
    /** Estado/província (admin1) — pode ser null para países sem divisão administrativa. */
    val admin1: String? = null,
    val country: String,
    /** Usado para ordenação por relevância (maior população primeiro). */
    val population: Int? = null,
    val timezone: String? = null
)
