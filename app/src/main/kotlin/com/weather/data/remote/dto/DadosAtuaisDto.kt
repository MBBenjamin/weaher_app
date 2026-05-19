package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para o bloco `current` da resposta da Open-Meteo Forecast API.
 *
 * Campos críticos são nullable conforme CE-05: campos ausentes são tratados
 * no mapper [PrevisaoMapper] com valores de fallback explícitos.
 */
@Serializable
data class DadosAtuaisDto(
    /** Hora da observação no formato ISO8601 ("2026-05-17T14:30"). */
    val time: String,
    @SerialName("temperature_2m") val temperature2m: Float? = null,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: Int? = null,
    @SerialName("apparent_temperature") val apparentTemperature: Float? = null,
    @SerialName("weather_code") val weatherCode: Int? = null,
    @SerialName("wind_speed_10m") val windSpeed10m: Float? = null,
    @SerialName("wind_direction_10m") val windDirection10m: Int? = null
)
