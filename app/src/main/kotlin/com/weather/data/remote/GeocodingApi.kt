package com.weather.data.remote

import com.weather.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Retrofit para o endpoint de geocoding da Open-Meteo Geocoding API.
 *
 * Base URL: `https://geocoding-api.open-meteo.com/` (domínio diferente do forecast).
 *
 * O campo `results` pode estar ausente na resposta quando nenhuma cidade é encontrada —
 * ver [GeocodingResponseDto] para tratamento correto.
 */
interface GeocodingApi {

    /**
     * Busca cidades pelo nome, retornando até [count] resultados ordenados por população.
     *
     * Não deve ser chamado com [nome] de menos de 2 caracteres (regra de negócio no
     * SearchViewModel com debounce 500ms).
     *
     * @param nome nome da cidade (mínimo 2 caracteres)
     */
    @GET("v1/search")
    suspend fun buscarCidades(
        @Query("name") nome: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "pt",
        @Query("format") format: String = "json"
    ): GeocodingResponseDto
}
