package com.weather.domain.model

import kotlinx.serialization.Serializable

/**
 * Dados meteorológicos para uma hora específica.
 *
 * Usado na [HourlyForecastSection] (listagem e gráfico Vico) e no [HourDetailSheet].
 */
@Serializable
data class HoraDados(
    /** Data ISO8601 ("2026-05-17") — chave para filtro por dia no ViewModel. */
    val dataIso: String,
    /** Hora formatada PT-BR ("14:00"). */
    val hora: String,
    val temperaturaC: Float,
    /** Precipitação acumulada no período em mm. */
    val precipitacaoMm: Float,
    val codigoWMO: Int,
    /** Umidade relativa 0–100 % (exibida no [HourDetailSheet]). */
    val umidadePercent: Int,
    /** Velocidade do vento em km/h (exibida no [HourDetailSheet]). */
    val velocidadeVentoKmh: Float,
    /** Direção do vento em graus 0–359 (exibida no [HourDetailSheet]). */
    val direcaoVentoGraus: Int
)
