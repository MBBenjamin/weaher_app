package com.weather.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = WeatherColors.Primary,
    surface = WeatherColors.Surface,
    background = WeatherColors.Background,
    onSurface = WeatherColors.OnSurface,
    onBackground = WeatherColors.OnSurface
)

/**
 * Tema principal do Weather App.
 *
 * Aplica a paleta [WeatherColors], a escala tipográfica [WeatherTypography]
 * e as formas [WeatherShapes] ao [MaterialTheme] do Compose.
 *
 * Dark mode não está no escopo do MVP — usa sempre o esquema claro.
 */
@Composable
fun WeatherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = WeatherTypography,
        shapes = WeatherShapes,
        content = content
    )
}
