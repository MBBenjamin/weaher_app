package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Metadados de unidades retornados pela Open-Meteo API para cada grupo de campos
 * (`current_units`, `hourly_units`, `daily_units`).
 *
 * Campos ausentes na resposta são tratados como null — a API pode omitir unidades
 * para campos não solicitados.
 */
@Serializable
data class UnidadesDto(
    val time: String? = null,
    @SerialName("temperature_2m") val temperature2m: String? = null,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: String? = null,
    @SerialName("apparent_temperature") val apparentTemperature: String? = null,
    @SerialName("weather_code") val weatherCode: String? = null,
    @SerialName("wind_speed_10m") val windSpeed10m: String? = null,
    @SerialName("wind_direction_10m") val windDirection10m: String? = null,
    val precipitation: String? = null,
    @SerialName("temperature_2m_max") val temperature2mMax: String? = null,
    @SerialName("temperature_2m_min") val temperature2mMin: String? = null,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: String? = null,
    @SerialName("wind_speed_10m_max") val windSpeed10mMax: String? = null
)
