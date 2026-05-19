package com.weather.presentation.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Placeholder animado com efeito shimmer para o [CurrentWeatherCard].
 *
 * Exibido enquanto o estado é [HomeUiState.Carregando].
 * Acessível via TalkBack com mensagem "Carregando previsão do tempo".
 */
@Composable
fun LoadingSkeleton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.4f),
        Color.LightGray.copy(alpha = 0.1f),
        Color.LightGray.copy(alpha = 0.4f)
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(shimmerX - 400f, 0f),
        end = Offset(shimmerX, 0f)
    )

    Column(
        modifier = modifier
            .semantics { contentDescription = "Carregando previsão do tempo" }
            .padding(16.dp)
    ) {
        // Placeholder do card principal (~60% da altura)
        ShimmerBox(brush = brush, modifier = Modifier.fillMaxWidth().height(260.dp))
        Spacer(Modifier.height(16.dp))
        // Placeholder do título de localização
        ShimmerBox(brush = brush, modifier = Modifier.fillMaxWidth(0.5f).height(20.dp))
        Spacer(Modifier.height(8.dp))
        // Placeholder da temperatura
        ShimmerBox(brush = brush, modifier = Modifier.fillMaxWidth(0.3f).height(48.dp))
        Spacer(Modifier.height(8.dp))
        // Placeholder das chips de dados
        ShimmerBox(brush = brush, modifier = Modifier.fillMaxWidth().height(40.dp))
    }
}

@Composable
private fun ShimmerBox(brush: Brush, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(brush, shape = RoundedCornerShape(8.dp))
    )
}
