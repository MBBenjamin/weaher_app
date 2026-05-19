package com.weather.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Escala tipográfica do design system Weather App.
 *
 * | Token           | Tamanho | Peso       | Uso principal                        |
 * |-----------------|---------|------------|--------------------------------------|
 * | displayLarge    | 57sp    | Bold       | Temperatura atual                    |
 * | headlineMedium  | 28sp    | SemiBold   | Nome da cidade / títulos de seção    |
 * | titleLarge      | 22sp    | Medium     | Cabeçalhos de cards                  |
 * | bodyLarge       | 16sp    | Regular    | Descrições e valores secundários     |
 * | labelSmall      | 11sp    | Regular    | Créditos, timestamps, labels menores |
 */
val WeatherTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 57.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
