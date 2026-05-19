package com.weather.domain.usecase

import com.weather.domain.model.Previsao
import com.weather.domain.repository.IPrevisaoRepository
import com.weather.utils.AppResult
import javax.inject.Inject

/**
 * Use case que encapsula a regra de buscar a previsão do tempo para uma localização.
 *
 * Delega para [IPrevisaoRepository], que decide entre cache e rede conforme
 * o estado de validade do cache e a conectividade disponível.
 */
class ObterPrevisaoUseCase @Inject constructor(
    private val repository: IPrevisaoRepository
) {
    suspend operator fun invoke(
        lat: Double,
        lon: Double,
        nomeLocalidade: String = "",
        forceRefresh: Boolean = false
    ): AppResult<Previsao> = repository.obterPrevisao(lat, lon, nomeLocalidade, forceRefresh)
}
