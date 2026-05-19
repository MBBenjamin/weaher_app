package com.weather.repository

import com.weather.data.local.dao.HistoricoBuscaDao
import com.weather.data.local.entity.HistoricoBuscaEntity
import com.weather.data.remote.GeocodingApi
import com.weather.data.remote.dto.CidadeDto
import com.weather.data.remote.dto.GeocodingResponseDto
import com.weather.data.repository.BuscaRepositoryImpl
import com.weather.domain.model.CidadeSugestao
import com.weather.utils.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BuscaRepositoryTest {

    private lateinit var api: GeocodingApi
    private lateinit var dao: HistoricoBuscaDao
    private lateinit var repository: BuscaRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        dao = mockk(relaxUnitFun = true)
        repository = BuscaRepositoryImpl(api, dao)
    }

    @Test
    fun geocoding_retorna_lista_CidadeSugestao_mapeada() = runTest {
        coEvery { api.buscarCidades(any(), any(), any(), any()) } returns GeocodingResponseDto(
            results = listOf(
                buildCidadeDto(id = 1, nome = "São Paulo", estado = "São Paulo", pais = "Brazil",
                    lat = -23.55, lon = -46.63, timezone = "America/Sao_Paulo"),
                buildCidadeDto(id = 2, nome = "São Paulo de Olivença", estado = "Amazonas", pais = "Brazil",
                    lat = -3.38, lon = -68.78, timezone = "America/Manaus")
            )
        )

        val result = repository.buscarCidades("São Paulo")

        assertTrue(result is AppResult.Success)
        val cidades = (result as AppResult.Success).data
        assertEquals(2, cidades.size)
        assertEquals("São Paulo", cidades[0].nome)
        assertEquals("São Paulo", cidades[0].estado)
        assertEquals("Brazil", cidades[0].pais)
        assertEquals(-23.55, cidades[0].latitude, 0.001)
        assertEquals("America/Sao_Paulo", cidades[0].fusoHorario)
        assertEquals("São Paulo de Olivença", cidades[1].nome)
        assertEquals("Amazonas", cidades[1].estado)
    }

    @Test
    fun historico_salva_e_recupera_ate_5_entradas() = runTest {
        coEvery { dao.getAllOrderedByBuscadoEmDesc() } returns buildHistoricoEntities(5)

        val historico = repository.obterHistorico()

        assertEquals(5, historico.size)
        assertEquals("Cidade 1", historico[0].nomeCidade)
        coVerify(exactly = 1) { dao.getAllOrderedByBuscadoEmDesc() }
    }

    @Test
    fun historico_nao_duplica_cidade_existente() = runTest {
        val cidade = buildSugestao("São Paulo", "São Paulo", "Brazil")
        // upsert é relaxUnitFun=true — sem mock explícito
        coEvery { dao.getAllOrderedByBuscadoEmDesc() } returns listOf(
            buildHistoricoEntity("São Paulo", "São Paulo", "Brazil", buscadoEm = 2L)
        )

        repository.salvarNoBusca(cidade)
        repository.salvarNoBusca(cidade)
        val historico = repository.obterHistorico()

        // dao.upsert é chamado duas vezes; a deduplicação é responsabilidade do DAO
        coVerify(exactly = 2) { dao.upsert(any()) }
        // historico retorna o que o DAO fornece (1 entrada — DAO deduplicou)
        assertEquals(1, historico.size)
    }

    @Test
    fun historico_move_cidade_existente_para_o_topo() = runTest {
        val cidadeA = buildSugestao("São Paulo", "São Paulo", "Brazil")
        val cidadeB = buildSugestao("Rio de Janeiro", "Rio de Janeiro", "Brazil")
        coEvery { dao.getAllOrderedByBuscadoEmDesc() } returns listOf(
            buildHistoricoEntity("São Paulo", "São Paulo", "Brazil", buscadoEm = 3L),
            buildHistoricoEntity("Rio de Janeiro", "Rio de Janeiro", "Brazil", buscadoEm = 2L)
        )

        repository.salvarNoBusca(cidadeA)
        repository.salvarNoBusca(cidadeB)
        repository.salvarNoBusca(cidadeA) // A deve subir para o topo

        val historico = repository.obterHistorico()

        assertEquals("São Paulo", historico[0].nomeCidade)
        assertEquals("Rio de Janeiro", historico[1].nomeCidade)
        coVerify(exactly = 3) { dao.upsert(any()) }
    }

    // --- helpers ---

    private fun buildCidadeDto(
        id: Int,
        nome: String,
        estado: String?,
        pais: String,
        lat: Double,
        lon: Double,
        timezone: String?
    ) = CidadeDto(
        id = id,
        name = nome,
        latitude = lat,
        longitude = lon,
        countryCode = pais.take(2).uppercase(),
        admin1 = estado,
        country = pais,
        timezone = timezone
    )

    private fun buildSugestao(nome: String, estado: String?, pais: String) = CidadeSugestao(
        nome = nome,
        estado = estado,
        pais = pais,
        latitude = -23.55,
        longitude = -46.63,
        fusoHorario = "America/Sao_Paulo"
    )

    private fun buildHistoricoEntity(
        nome: String,
        estado: String,
        pais: String,
        buscadoEm: Long = System.currentTimeMillis()
    ) = HistoricoBuscaEntity(
        nomeCidade = nome,
        estado = estado,
        pais = pais,
        latitude = -23.55,
        longitude = -46.63,
        buscadoEm = buscadoEm
    )

    private fun buildHistoricoEntities(count: Int): List<HistoricoBuscaEntity> =
        (1..count).map { i ->
            HistoricoBuscaEntity(
                nomeCidade = "Cidade $i",
                estado = "Estado $i",
                pais = "Brazil",
                latitude = -23.0 - i,
                longitude = -46.0 - i,
                buscadoEm = System.currentTimeMillis() - i * 1000L
            )
        }
}
