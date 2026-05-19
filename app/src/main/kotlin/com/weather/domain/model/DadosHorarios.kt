package com.weather.domain.model

import kotlinx.serialization.Serializable

/**
 * Previsão horária para os próximos 7 dias (168 entradas: 24h × 7 dias).
 *
 * O [HomeViewModel] filtra [horas] por [HoraDados.dataIso] para exibir as 24h
 * do dia corrente ou do dia selecionado no [DayDetailSheet].
 */
@Serializable
data class DadosHorarios(
    val horas: List<HoraDados>
)
