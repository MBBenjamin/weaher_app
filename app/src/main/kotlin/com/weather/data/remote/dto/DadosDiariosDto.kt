package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para o bloco `daily` da resposta da Open-Meteo Forecast API.
 *
 * Contém exatamente 7 entradas (uma por dia). Elementos das listas são nullable
 * conforme CE-05.
 *
 * Inclui direção dominante do vento e umidade máxima necessários para o [DayDetailSheet].
 */
@Serializable
data class DadosDiariosDto(
    /** Lista de datas ISO8601 ("2026-05-17"). */
    val time: List<String>,
    @SerialName("temperature_2m_max") val temperature2mMax: List<Float?>,
    @SerialName("temperature_2m_min") val temperature2mMin: List<Float?>,
    @SerialName("weather_code") val weatherCode: List<Int?>,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int?>,
    @SerialName("wind_speed_10m_max") val windSpeed10mMax: List<Float?>,
    @SerialName("wind_direction_10m_dominant") val windDirection10mDominant: List<Int?>,
    @SerialName("relative_humidity_2m_max") val relativeHumidity2mMax: List<Int?>
)
