package com.weather.domain.model

import kotlinx.serialization.Serializable

/**
 * Previsão diária para os próximos 7 dias.
 *
 * O [HomeViewModel] exibe [dias] na [WeeklyForecastList].
 * O primeiro item tem [DiaDados.eHoje] = true.
 */
@Serializable
data class DadosDiarios(
    val dias: List<DiaDados>
)
