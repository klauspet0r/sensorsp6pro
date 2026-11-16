package dev.sensorsp6pro.explorer.feature.motion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import dev.sensorsp6pro.explorer.core.domain.AccelerationMeasurement
import dev.sensorsp6pro.explorer.core.domain.SensorAccuracy
import dev.sensorsp6pro.explorer.core.domain.Vector3
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class AccelerationScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun `shows values explanation and pause action`() {
        var active = true
        val sample = AccelerationMeasurement(Vector3(0.12f, 0.08f, 9.81f), 1L, SensorAccuracy.HIGH)
        compose.setContent {
            AccelerationContent(
                state = AccelerationUiState(MeasurementStatus.LIVE, listOf(sample)),
                onActiveChange = { active = it },
            )
        }
        compose.onNodeWithText("Z\n9,81 m/s²").assertIsDisplayed()
        compose.onNodeWithText("Probier es aus").assertIsDisplayed()
        compose.onNodeWithText("Messung pausieren")
            .performSemanticsAction(SemanticsActions.OnClick)
        compose.runOnIdle {
            assertFalse(active)
        }
    }
}
