package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO raiz da resposta do endpoint `/v1/search` da Open-Meteo Geocoding API.
 *
 * O campo `results` é **ausente** (não `null`, não `[]`) quando nenhuma cidade
 * corresponde ao termo buscado — por isso é nullable com default `null`.
 * Usar `results.orEmpty()` nos consumidores para tratar os dois casos.
 */
@Serializable
data class GeocodingResponseDto(
    /** Null quando a API não encontra resultados (campo omitido na resposta). */
    val results: List<CidadeDto>? = null,
    @SerialName("generationtime_ms") val generationtimeMs: Float? = null
)
