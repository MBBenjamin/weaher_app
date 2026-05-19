package com.weather.domain.usecase

import com.weather.domain.model.CidadeSugestao
import com.weather.domain.repository.IBuscaRepository
import com.weather.utils.AppResult
import javax.inject.Inject

/**
 * Caso de uso para busca de cidades via geocoding.
 *
 * Delega para [IBuscaRepository.buscarCidades] e retorna até 5 sugestões.
 */
class BuscarCidadesUseCase @Inject constructor(
    private val repository: IBuscaRepository
) {
    suspend operator fun invoke(query: String): AppResult<List<CidadeSugestao>> =
        repository.buscarCidades(query)
}
