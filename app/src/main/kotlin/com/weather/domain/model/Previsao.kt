package com.weather.domain.model

import kotlinx.serialization.Serializable

/**
 * Previsão meteorológica completa para uma localização.
 *
 * Armazenada em [PrevisaoEntity.dadosJson] como JSON serializado.
 * Use [Previsao.toEntity] para persistir e [PrevisaoEntity.toDomain] para recuperar.
 */
@Serializable
data class Previsao(
    val latitude: Double,
    val longitude: Double,
    val nomeLocalidade: String,
    val fusoHorario: String,
    val atual: DadosAtuais,
    val horario: DadosHorarios,
    val diario: DadosDiarios,
    /** Epoch ms do momento em que os dados foram buscados da API. */
    val timestampAtualizado: Long
)
