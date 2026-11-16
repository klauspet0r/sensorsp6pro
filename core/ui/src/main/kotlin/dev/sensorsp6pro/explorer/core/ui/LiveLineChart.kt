package dev.sensorsp6pro.explorer.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.sensorsp6pro.explorer.core.domain.Vector3

@Composable
fun LiveLineChart(history: List<Vector3>, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(160.dp)) {
        if (history.isEmpty()) return@Canvas
        val allValues = history.flatMap { listOf(it.x, it.y, it.z) }
        val minimum = allValues.minOrNull() ?: 0f
        val maximum = allValues.maxOrNull() ?: 1f
        val range = (maximum - minimum).takeIf { it > 0.001f } ?: 1f
        val colors = listOf(Color(0xFFB24D5A), Color(0xFF347CA5), Color(0xFF438365))
        val selectors = listOf(Vector3::x, Vector3::y, Vector3::z)
        selectors.forEachIndexed { index, selector ->
            val path = Path()
            history.forEachIndexed { pointIndex, point ->
                val x = if (history.size == 1) 0f else pointIndex.toFloat() / (history.lastIndex) * size.width
                val y = size.height - ((selector(point) - minimum) / range) * size.height
                if (pointIndex == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, colors[index], style = Stroke(width = 3f))
        }
    }
}
