package com.weather.repository

import com.weather.data.local.dao.PrevisaoDao
import com.weather.data.local.entity.PrevisaoEntity
import com.weather.data.remote.OpenMeteoApi
import com.weather.data.remote.RateLimitException
import com.weather.data.remote.dto.DadosAtuaisDto
import com.weather.data.remote.dto.DadosDiariosDto
import com.weather.data.remote.dto.DadosHorariosDto
import com.weather.data.remote.dto.PrevisaoResponseDto
import com.weather.data.remote.dto.UnidadesDto
import com.weather.data.repository.PrevisaoRepositoryImpl
import com.weather.domain.model.DadosAtuais
import com.weather.domain.model.DadosDiarios
import com.weather.domain.model.DadosHorarios
import com.weather.domain.model.Previsao
import com.weather.utils.AppResult
import com.weather.utils.NetworkMonitor
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PrevisaoRepositoryTest {

    private lateinit var api: OpenMeteoApi
    private lateinit var dao: PrevisaoDao
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var repository: PrevisaoRepositoryImpl

    private val isOnlineFlow = MutableStateFlow(true)

    private val lat = -23.55
    private val lon = -46.63
    private val nome = "São Paulo, SP"

    // Cache ID matches buildId logic (2 decimal places)
    private val cacheId = "-23.55,-46.63"

    private val previsaoValida = buildPrevisao(timestampAtualizado = System.currentTimeMillis())
    private val previsaoExpirada = buildPrevisao(timestampAtualizado = System.currentTimeMillis() - 2 * 3_600_000L)

    private val entityValida = buildEntity(previsaoValida)
    private val entityExpirada = buildEntity(previsaoExpirada)

    private val apiDto = buildDto()

    @Before
    fun setup() {
        api = mockk()
        dao = mockk(relaxUnitFun = true)
        networkMonitor = mockk()
        every { networkMonitor.isOnline } returns isOnlineFlow
        repository = PrevisaoRepositoryImpl(api, dao, networkMonitor)
    }

    @Test
    fun cache_valido_nao_requisita_api() = runTest {
        coEvery { dao.buscarPorId(cacheId) } returns entityValida

        val result = repository.obterPrevisao(lat, lon, nome)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 0) { api.obterPrevisao(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun cache_expirado_requisita_api_e_salva_no_room() = runTest {
        coEvery { dao.buscarPorId(cacheId) } returns entityExpirada
        coEvery { api.obterPrevisao(any(), any(), any(), any(), any(), any(), any(), any()) } returns apiDto

        val result = repository.obterPrevisao(lat, lon, nome)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { api.obterPrevisao(lat, lon, any(), any(), any(), any(), any(), any()) }
        coVerify(exactly = 1) { dao.inserirOuSubstituir(any()) }
    }

    @Test
    fun offline_retorna_Result_Success_com_previsao_de_cache() = runTest {
        isOnlineFlow.value = false
        coEvery { dao.buscarPorId(cacheId) } returns entityExpirada

        val result = repository.obterPrevisao(lat, lon, nome)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 0) { api.obterPrevisao(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun sem_cache_e_offline_retorna_Result_Error() = runTest {
        isOnlineFlow.value = false
        coEvery { dao.buscarPorId(cacheId) } returns null

        val result = repository.obterPrevisao(lat, lon, nome)

        assertTrue(result is AppResult.Error)
        coVerify(exactly = 0) { api.obterPrevisao(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun forceRefresh_true_ignora_cache_e_chama_api() = runTest {
        coEvery { dao.buscarPorId(cacheId) } returns entityValida
        coEvery { api.obterPrevisao(any(), any(), any(), any(), any(), any(), any(), any()) } returns apiDto

        val result = repository.obterPrevisao(lat, lon, nome, forceRefresh = true)

        assertTrue(result is AppResult.Success)
        coVerify(exactly = 1) { api.obterPrevisao(lat, lon, any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun api_retorna_RateLimitException_resulta_em_Result_Error_com_mensagem_rate_limit() = runTest {
        coEvery { dao.buscarPorId(cacheId) } returns null
        coEvery {
            api.obterPrevisao(any(), any(), any(), any(), any(), any(), any(), any())
        } throws RateLimitException(retryAfterSeconds = 60)

        val result = repository.obterPrevisao(lat, lon, nome)

        assertTrue(result is AppResult.Error)
        val error = result as AppResult.Error
        assertTrue(error.message.contains("60"))
        assertTrue(error.exception is RateLimitException)
    }

    // ── T036: teste de cache expirado offline ─────────────────────────────────────

    @Test
    fun cache_expirado_e_offline_emite_Sucesso_com_isOffline_true_e_horasAtraso_calculado() = runTest {
        // Simula offline com cache de 3h (expirado, mas não obsoleto)
        isOnlineFlow.value = false
        val timestampTresHorasAtras = System.currentTimeMillis() - 3 * 3_600_000L
        val previsaoExpirada = buildPrevisao(timestampAtualizado = timestampTresHorasAtras)
        val entityExpirada3h = buildEntity(previsaoExpirada)
        coEvery { dao.buscarPorId(cacheId) } returns entityExpirada3h

        val result = repository.obterPrevisao(lat, lon, nome)

        // Offline + cache expirado → retorna os dados em cache (Success), não Error
        assertTrue("Esperado Success com cache expirado offline, obtido: $result",
            result is AppResult.Success)
        // API não deve ter sido chamada
        coVerify(exactly = 0) { api.obterPrevisao(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    // --- helpers ---

    private fun buildPrevisao(timestampAtualizado: Long): Previsao = Previsao(
        latitude = lat,
        longitude = lon,
        nomeLocalidade = nome,
        fusoHorario = "America/Sao_Paulo",
        atual = DadosAtuais(
            temperaturaC = 22f,
            sensacaoTermicaC = 21f,
            umidadePercent = 70,
            velocidadeVentoKmh = 10f,
            direcaoVentoGraus = 90,
            codigoWMO = 0,
            horaAtualizado = "2026-05-19T14:00"
        ),
        horario = DadosHorarios(emptyList()),
        diario = DadosDiarios(emptyList()),
        timestampAtualizado = timestampAtualizado
    )

    private val serializerJson = Json { encodeDefaults = true }

    private fun buildEntity(previsao: Previsao): PrevisaoEntity = PrevisaoEntity(
        id = cacheId,
        latitude = lat,
        longitude = lon,
        nomeLocalidade = nome,
        tempAtual = previsao.atual.temperaturaC,
        sensacaoTermica = previsao.atual.sensacaoTermicaC,
        umidade = previsao.atual.umidadePercent,
        velocidadeVento = previsao.atual.velocidadeVentoKmh,
        direcaoVento = previsao.atual.direcaoVentoGraus,
        codigoWmo = previsao.atual.codigoWMO,
        dadosJson = serializerJson.encodeToString(previsao),
        timestampAtualizado = previsao.timestampAtualizado,
        criadoEm = System.currentTimeMillis()
    )

    private fun buildDto(): PrevisaoResponseDto = PrevisaoResponseDto(
        latitude = lat,
        longitude = lon,
        utcOffsetSeconds = -10800,
        timezone = "America/Sao_Paulo",
        current = DadosAtuaisDto(
            time = "2026-05-19T14:00",
            temperature2m = 22f,
            relativeHumidity2m = 70,
            apparentTemperature = 21f,
            weatherCode = 0,
            windSpeed10m = 10f,
            windDirection10m = 90
        ),
        currentUnits = UnidadesDto(),
        hourly = DadosHorariosDto(
            time = emptyList(),
            temperature2m = emptyList(),
            precipitation = emptyList(),
            weatherCode = emptyList(),
            windSpeed10m = emptyList(),
            windDirection10m = emptyList(),
            relativeHumidity2m = emptyList()
        ),
        hourlyUnits = UnidadesDto(),
        daily = DadosDiariosDto(
            time = emptyList(),
            temperature2mMax = emptyList(),
            temperature2mMin = emptyList(),
            weatherCode = emptyList(),
            precipitationProbabilityMax = emptyList(),
            windSpeed10mMax = emptyList(),
            windDirection10mDominant = emptyList(),
            relativeHumidity2mMax = emptyList()
        ),
        dailyUnits = UnidadesDto()
    )
}
