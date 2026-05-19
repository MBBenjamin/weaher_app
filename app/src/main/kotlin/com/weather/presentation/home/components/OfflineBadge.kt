package com.weather.presentation.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Badge vermelho que indica modo offline com dados desatualizados.
 *
 * Visível quando [HomeUiState.Sucesso.isOffline] é `true`.
 *
 * @param horasAtraso horas desde a última atualização (0 = menos de 1h)
 */
@Composable
fun OfflineBadge(horasAtraso: Int, modifier: Modifier = Modifier) {
    val textoAtraso = if (horasAtraso > 0) "Dados de há ${horasAtraso}h" else "Dados de cache"
    val descricaoAcessibilidade = "Sem internet. $textoAtraso"

    Surface(
        modifier = modifier.semantics { contentDescription = descricaoAcessibilidade },
        shape = MaterialTheme.shapes.small,
        color = Color(0xFFB71C1C).copy(alpha = 0.12f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = Color(0xFFB71C1C),
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "OFFLINE · $textoAtraso",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFB71C1C)
            )
        }
    }
}
