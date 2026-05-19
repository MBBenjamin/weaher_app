package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO raiz da resposta do endpoint `/v1/forecast` da Open-Meteo API.
 *
 * Agrega os blocos `current`, `hourly` e `daily` junto com seus respectivos
 * metadados de unidades. Mapeado para o modelo de domínio [Previsao] via
 * [PrevisaoMapper].
 */
@Serializable
data class PrevisaoResponseDto(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null,
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int,
    val timezone: String,
    val current: DadosAtuaisDto,
    @SerialName("current_units") val currentUnits: UnidadesDto,
    val hourly: DadosHorariosDto,
    @SerialName("hourly_units") val hourlyUnits: UnidadesDto,
    val daily: DadosDiariosDto,
    @SerialName("daily_units") val dailyUnits: UnidadesDto
)
