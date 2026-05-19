package com.weather.domain.repository

import com.weather.domain.model.Previsao
import com.weather.utils.AppResult

interface IPrevisaoRepository {
    suspend fun obterPrevisao(
        lat: Double,
        lon: Double,
        nomeLocalidade: String,
        forceRefresh: Boolean = false
    ): AppResult<Previsao>
}
