package dev.sensorsp6pro.explorer.feature.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import dev.sensorsp6pro.explorer.feature.motion.AccelerationViewModel

@Composable
fun DashboardScreen(
    onOpenAcceleration: () -> Unit,
    viewModel: AccelerationViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.setActive(true)
        onPauseOrDispose { viewModel.setActive(false) }
    }
    Column(Modifier.padding(16.dp)) {
        Text("Sensor Explorer", style = MaterialTheme.typography.headlineMedium)
        Card(onClick = onOpenAcceleration, modifier = Modifier.padding(top = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Beschleunigung", style = MaterialTheme.typography.titleMedium)
                val sample = state.history.lastOrNull()
                Text(sample?.let { "Z: %.2f m/s²".format(it.vector.z) } ?: "Sensor wird gestartet")
            }
        }
    }
}
