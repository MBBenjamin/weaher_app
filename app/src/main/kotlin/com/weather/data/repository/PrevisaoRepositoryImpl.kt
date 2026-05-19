package com.weather.data.repository

import com.weather.data.local.dao.PrevisaoDao
import com.weather.data.mapper.toDomain
import com.weather.data.mapper.toEntity
import com.weather.data.remote.OpenMeteoApi
import com.weather.data.remote.RateLimitException
import com.weather.domain.model.Previsao
import com.weather.domain.repository.IPrevisaoRepository
import com.weather.utils.AppResult
import com.weather.utils.CacheValidator
import com.weather.utils.NetworkMonitor
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class PrevisaoRepositoryImpl @Inject constructor(
    private val api: OpenMeteoApi,
    private val dao: PrevisaoDao,
    private val networkMonitor: NetworkMonitor
) : IPrevisaoRepository {

    override suspend fun obterPrevisao(
        lat: Double,
        lon: Double,
        nomeLocalidade: String,
        forceRefresh: Boolean
    ): AppResult<Previsao> {
        val id = buildId(lat, lon)
        val cached = dao.buscarPorId(id)

        if (!forceRefresh && cached != null && CacheValidator.estaValido(cached.timestampAtualizado)) {
            return AppResult.Success(cached.toDomain())
        }

        val isOnline = networkMonitor.isOnline.first()
        if (!isOnline) {
            return if (cached != null) {
                AppResult.Success(cached.toDomain())
            } else {
                AppResult.Error("Sem conexão com a internet e nenhum dado em cache.")
            }
        }

        return try {
            val dto = api.obterPrevisao(latitude = lat, longitude = lon)
            val previsao = dto.toDomain(nomeLocalidade)
            dao.inserirOuSubstituir(previsao.toEntity(nomeLocalidade))
            AppResult.Success(previsao)
        } catch (e: RateLimitException) {
            Timber.w(e, "Rate limit atingido")
            AppResult.Error(
                message = "Limite de requisições atingido. Tente novamente em ${e.retryAfterSeconds}s.",
                exception = e
            )
        } catch (e: Exception) {
            Timber.e(e, "Erro ao buscar previsão")
            if (cached != null) {
                AppResult.Success(cached.toDomain())
            } else {
                AppResult.Error(message = e.message ?: "Erro desconhecido", exception = e)
            }
        }
    }

    private fun buildId(lat: Double, lon: Double): String {
        val latR = (lat * 100).roundToInt() / 100.0
        val lonR = (lon * 100).roundToInt() / 100.0
        return "$latR,$lonR"
    }
}
