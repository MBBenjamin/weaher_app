package com.weather.data.remote

import com.weather.data.remote.dto.PrevisaoResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Retrofit para o endpoint de previsão da Open-Meteo API.
 *
 * Base URL: `https://api.open-meteo.com/`
 *
 * Todos os parâmetros têm valores padrão alinhados com a spec §5.2.
 * O interceptor [RateLimitInterceptor] trata HTTP 429 antes de chegar aqui.
 */
interface OpenMeteoApi {

    /**
     * Busca condições atuais + previsão horária (168h) + previsão diária (7 dias).
     *
     * @param latitude coordenada −90.0 a 90.0
     * @param longitude coordenada −180.0 a 180.0
     * @param forceRefresh ignorado pela API — usado apenas como flag no repositório
     */
    @GET("v1/forecast")
    suspend fun obterPrevisao(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_FIELDS,
        @Query("hourly") hourly: String = HOURLY_FIELDS,
        @Query("daily") daily: String = DAILY_FIELDS,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("wind_speed_unit") windSpeedUnit: String = "kmh"
    ): PrevisaoResponseDto

    companion object {
        const val CURRENT_FIELDS =
            "temperature_2m,relative_humidity_2m,apparent_temperature," +
            "weather_code,wind_speed_10m,wind_direction_10m"

        /** Inclui vento e umidade horários para o HourDetailSheet. */
        const val HOURLY_FIELDS =
            "temperature_2m,precipitation,weather_code," +
            "wind_speed_10m,wind_direction_10m,relative_humidity_2m"

        const val DAILY_FIELDS =
            "temperature_2m_max,temperature_2m_min,weather_code," +
            "precipitation_probability_max,wind_speed_10m_max," +
            "wind_direction_10m_dominant,relative_humidity_2m_max"
    }
}
