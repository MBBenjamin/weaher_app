package com.weather.domain.model

import kotlinx.serialization.Serializable

/** Resultado de geocodificação usado na tela de busca. */
@Serializable
data class Cidade(
    val nome: String,
    val pais: String,
    val estado: String?,
    val latitude: Double,
    val longitude: Double,
    val fusoHorario: String?
)
