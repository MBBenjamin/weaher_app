package com.weather.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Formas do design system Weather App.
 *
 * | Token     | Raio  | Uso                                      |
 * |-----------|-------|------------------------------------------|
 * | small     | 12dp  | Chips, badges, botões pequenos           |
 * | medium    | 16dp  | Cards informativos, bottom sheets        |
 * | large     | 24dp  | Card principal de temperatura (hero)     |
 */
val WeatherShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)
