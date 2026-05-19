package com.weather.data.repository

import com.weather.data.local.dao.HistoricoBuscaDao
import com.weather.data.mapper.toDomain
import com.weather.data.mapper.toHistoricoEntity
import com.weather.data.remote.GeocodingApi
import com.weather.domain.model.CidadeSugestao
import com.weather.domain.model.HistoricoBusca
import com.weather.domain.repository.IBuscaRepository
import com.weather.utils.AppResult
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuscaRepositoryImpl @Inject constructor(
    private val api: GeocodingApi,
    private val dao: HistoricoBuscaDao
) : IBuscaRepository {

    override suspend fun buscarCidades(nome: String): AppResult<List<CidadeSugestao>> =
        try {
            val response = api.buscarCidades(nome)
            val cidades = response.results.orEmpty().map { it.toDomain() }
            AppResult.Success(cidades)
        } catch (e: Exception) {
            Timber.e(e, "Erro ao buscar cidades para: $nome")
            AppResult.Error(message = e.message ?: "Erro ao buscar cidades", exception = e)
        }

    override suspend fun salvarNoBusca(cidade: CidadeSugestao) {
        dao.upsert(cidade.toHistoricoEntity())
    }

    override suspend fun obterHistorico(): List<HistoricoBusca> =
        dao.getAllOrderedByBuscadoEmDesc().map { it.toDomain() }
}
