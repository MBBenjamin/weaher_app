package com.weather.domain.model

import kotlinx.serialization.Serializable

/**
 * Dados meteorológicos resumidos para um dia.
 *
 * Usado nos cards da [WeeklyForecastList] e nas abas do [DayDetailSheet].
 */
@Serializable
data class DiaDados(
    /** Data formatada PT-BR ("Sex, 17 mai") para exibição nos cards. */
    val data: String,
    /** Data ISO8601 ("2026-05-17") — chave para lookup de horas no ViewModel. */
    val dataIso: String,
    val temperaturaMaxC: Float,
    val temperaturaMinC: Float,
    /** Probabilidade de chuva 0–100 %. */
    val probChuvaPercent: Int,
    val velocidadeMaxVentoKmh: Float,
    /** Direção dominante do vento em graus (aba "Índices" do [DayDetailSheet]). */
    val direcaoDominanteVentoGraus: Int,
    /** Umidade máxima do dia 0–100 % (aba "Índices" do [DayDetailSheet]). */
    val umidadeMaxPercent: Int,
    val codigoWMO: Int,
    /** `true` apenas para o primeiro item da lista (dia de hoje). */
    val eHoje: Boolean
)
