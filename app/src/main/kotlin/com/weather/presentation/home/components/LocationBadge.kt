package com.weather.presentation.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Badge exibido enquanto o GPS ainda está refinando a localização.
 *
 * Visível apenas quando [HomeUiState.Sucesso.isLocalizacaoAproximada] é `true`.
 * O ícone de localização pisca para indicar atividade.
 */
@Composable
fun LocationBadge(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "location_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "location_icon_alpha"
    )

    Surface(
        modifier = modifier.semantics {
            contentDescription = "Localização aproximada, refinando com GPS"
        },
        shape = MaterialTheme.shapes.small,
        color = Color(0xFF0288D1).copy(alpha = 0.15f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF0288D1),
                modifier = Modifier
                    .size(16.dp)
                    .alpha(alpha)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Localização aproximada · refinando...",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF0288D1)
            )
        }
    }
}
