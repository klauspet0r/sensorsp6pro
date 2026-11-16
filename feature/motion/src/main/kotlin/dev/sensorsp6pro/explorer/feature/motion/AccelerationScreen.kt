package dev.sensorsp6pro.explorer.feature.motion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sensorsp6pro.explorer.core.ui.ExperimentCard
import dev.sensorsp6pro.explorer.core.ui.LiveLineChart

@Composable
fun AccelerationScreen(viewModel: AccelerationViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.setActive(true)
        onPauseOrDispose { viewModel.setActive(false) }
    }
    AccelerationContent(state, viewModel::setActive)
}

@Composable
fun AccelerationContent(
    state: AccelerationUiState,
    onActiveChange: (Boolean) -> Unit,
) {
    val sample = state.history.lastOrNull()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Beschleunigung", style = MaterialTheme.typography.headlineMedium)
        Text("Status: ${state.status.name.lowercase()}")
        if (sample != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("X\n" + String.format(java.util.Locale.GERMANY, "%.2f m/s²", sample.vector.x))
                Text("Y\n" + String.format(java.util.Locale.GERMANY, "%.2f m/s²", sample.vector.y))
                Text("Z\n" + String.format(java.util.Locale.GERMANY, "%.2f m/s²", sample.vector.z))
            }
            LiveLineChart(state.history.map { it.vector })
            Text("Genauigkeit: ${sample.accuracy.name.lowercase()}")
        }
        Card(Modifier.fillMaxWidth()) {
            Text(
                "Der Sensor misst Beschleunigung einschließlich der Erdanziehung. " +
                    "Deshalb zeigt er auch bei ruhendem Handy ungefähr 9,81 m/s².",
                Modifier.padding(16.dp),
            )
        }
        ExperimentCard("Lege dein Handy flach auf den Tisch. Kippe es danach langsam und beobachte die drei Achsen.")
        state.message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = { onActiveChange(state.status == MeasurementStatus.PAUSED) }) {
            Text(if (state.status == MeasurementStatus.PAUSED) "Messung fortsetzen" else "Messung pausieren")
        }
    }
}
