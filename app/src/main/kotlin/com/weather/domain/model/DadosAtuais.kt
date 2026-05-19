package com.weather.domain.model

import kotlinx.serialization.Serializable

/**
 * Condições meteorológicas atuais para uma localização.
 *
 * Todos os campos usam valores de fallback definidos no [PrevisaoMapper] (CE-05):
 * temperatura e vento ficam em 0f, código WMO fica em 45 (névoa neutro).
 */
@Serializable
data class DadosAtuais(
    /** Temperatura em °C. */
    val temperaturaC: Float,
    /** Sensação térmica em °C. */
    val sensacaoTermicaC: Float,
    /** Umidade relativa 0–100 %. */
    val umidadePercent: Int,
    /** Velocidade do vento em km/h. */
    val velocidadeVentoKmh: Float,
    /** Direção do vento em graus 0–359. */
    val direcaoVentoGraus: Int,
    /** Código WMO 0–99 representando a condição climática. */
    val codigoWMO: Int,
    /** Hora da observação no formato ISO8601 ("2026-05-17T14:30"). */
    val horaAtualizado: String
)
