package com.weather.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.weather.BuildConfig
import com.weather.data.remote.FirebaseMetricsInterceptor
import com.weather.data.remote.GeocodingApi
import com.weather.data.remote.OpenMeteoApi
import com.weather.data.remote.RateLimitInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo Hilt que provê as dependências de rede: OkHttp, Retrofit (forecast e geocoding)
 * e as interfaces da API.
 *
 * Dois Retrofit distintos são necessários porque forecast e geocoding usam base URLs
 * diferentes (api.open-meteo.com vs geocoding-api.open-meteo.com).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val networkJson = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        rateLimitInterceptor: RateLimitInterceptor,
        firebaseMetricsInterceptor: FirebaseMetricsInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        )
        .addInterceptor(rateLimitInterceptor)
        .addInterceptor(firebaseMetricsInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    /** Retrofit para o endpoint de previsão (api.open-meteo.com). */
    @Provides
    @Singleton
    fun provideOpenMeteoApi(okHttpClient: OkHttpClient): OpenMeteoApi =
        retrofit("https://api.open-meteo.com/", okHttpClient)
            .create(OpenMeteoApi::class.java)

    /** Retrofit para o endpoint de geocoding (geocoding-api.open-meteo.com). */
    @Provides
    @Singleton
    fun provideGeocodingApi(okHttpClient: OkHttpClient): GeocodingApi =
        retrofit("https://geocoding-api.open-meteo.com/", okHttpClient)
            .create(GeocodingApi::class.java)

    private fun retrofit(baseUrl: String, client: OkHttpClient) =
        retrofit2.Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType())
            )
            .build()
}
