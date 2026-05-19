package com.weather.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.weather.domain.model.HoraDados
import com.weather.utils.WindDirectionMapper
import com.weather.utils.WmoMapper
import kotlin.math.roundToInt

/**
 * Bottom sheet de detalhe para uma hora selecionada na previsão horária.
 *
 * Exibe ícone WMO, temperatura, umidade, vento (velocidade + cardinal + graus)
 * e descrição completa da condição climática.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HourDetailSheet(
    hora: HoraDados,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val descricaoWmo = stringResource(WmoMapper.descricaoWMO(hora.codigoWMO))
    val cardinal = WindDirectionMapper.paraCardinal(hora.direcaoVentoGraus)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = hora.hora,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics {
                    contentDescription = "Hora: ${hora.hora}"
                }
            )

            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(WmoMapper.iconeWMO(hora.codigoWMO)),
                contentDescription = descricaoWmo,
                modifier = Modifier.size(64.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = descricaoWmo,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(24.dp))

            DetailRow(
                label = "Temperatura",
                value = "${hora.temperaturaC.roundToInt()}°C"
            )

            DetailRow(
                label = "Umidade",
                value = "${hora.umidadePercent}%"
            )

            DetailRow(
                label = "Vento",
                value = "${"%.1f".format(hora.velocidadeVentoKmh)} km/h · $cardinal (${hora.direcaoVentoGraus}°)"
            )

            if (hora.precipitacaoMm > 0f) {
                DetailRow(
                    label = "Precipitação",
                    value = "${"%.1f".format(hora.precipitacaoMm)} mm"
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$label: $value"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
