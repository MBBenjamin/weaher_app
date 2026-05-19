package com.weather.presentation.theme

import androidx.compose.ui.graphics.Color

/** Paleta cromática do design system Weather App. */
object WeatherColors {
    val Primary = Color(0xFF0288D1)
    val Surface = Color(0xFFFAFAFA)
    val Background = Color(0xFFF0F4F8)
    val OnSurface = Color(0xFF1A1A2E)
    val TempMax = Color(0xFFEF5350)
    val TempMin = Color(0xFF42A5F5)

    /** Precipitação com 40% de opacidade. */
    val Precip = Color(0x6629B6F6)

    // Gradientes por condição climática
    private val GradientSunny = listOf(Color(0xFFFFF9C4), Color(0xFFFFCC02), Color(0xFFFFA726))
    private val GradientPartlyCloudy = listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9), Color(0xFF42A5F5))
    private val GradientCloudy = listOf(Color(0xFFECEFF1), Color(0xFFB0BEC5), Color(0xFF78909C))
    private val GradientRainy = listOf(Color(0xFFE3F2FD), Color(0xFF29B6F6), Color(0xFF0277BD))
    private val GradientStorm = listOf(Color(0xFF37474F), Color(0xFF455A64), Color(0xFF263238))
    private val GradientFog = listOf(Color(0xFFF5F5F5), Color(0xFFDDDDDD), Color(0xFFBBBBBB))
    private val GradientSnow = listOf(Color(0xFFF8FBFF), Color(0xFFE3F2FD), Color(0xFFBBDEFB))

    /**
     * Retorna o gradiente apropriado para um código WMO.
     *
     * Grupos:
     * - 0: ensolarado
     * - 1-3: parcialmente nublado / nublado
     * - 45-48: neblina
     * - 51-67: chuvisco / chuva
     * - 71-77: neve
     * - 80-82: pancadas
     * - 85-86: pancadas de neve
     * - 95-99: tempestade
     *
     * Código nulo ou inválido retorna gradiente de céu claro.
     */
    fun gradientForWmoCode(code: Int?): List<Color> = when (code) {
        0 -> GradientSunny
        1 -> GradientPartlyCloudy
        2, 3 -> GradientCloudy
        45, 48 -> GradientFog
        in 51..67 -> GradientRainy
        in 71..77 -> GradientSnow
        in 80..82 -> GradientRainy
        85, 86 -> GradientSnow
        in 95..99 -> GradientStorm
        else -> GradientSunny
    }
}
