package dev.sensorsp6pro.explorer.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExperimentCard(text: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text("Probier es aus", style = MaterialTheme.typography.titleMedium)
            Text(text, Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
