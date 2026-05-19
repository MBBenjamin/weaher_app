package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para o bloco `hourly` da resposta da Open-Meteo Forecast API.
 *
 * Contém 168 entradas (24h × 7 dias). Elementos individuais das listas são nullable
 * conforme CE-05 — a API pode retornar null para horas sem dados disponíveis.
 *
 * Inclui campos de vento e umidade necessários para o [HourDetailSheet].
 */
@Serializable
data class DadosHorariosDto(
    /** Lista de timestamps ISO8601 ("2026-05-17T00:00"). */
    val time: List<String>,
    @SerialName("temperature_2m") val temperature2m: List<Float?>,
    val precipitation: List<Float?>,
    @SerialName("weather_code") val weatherCode: List<Int?>,
    @SerialName("wind_speed_10m") val windSpeed10m: List<Float?>,
    @SerialName("wind_direction_10m") val windDirection10m: List<Int?>,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: List<Int?>
)
