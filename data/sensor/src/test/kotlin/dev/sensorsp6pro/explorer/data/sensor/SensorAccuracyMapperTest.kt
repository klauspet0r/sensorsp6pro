package dev.sensorsp6pro.explorer.data.sensor

import android.hardware.SensorManager
import dev.sensorsp6pro.explorer.core.domain.SensorAccuracy
import org.junit.Assert.assertEquals
import org.junit.Test

class SensorAccuracyMapperTest {
    @Test
    fun `maps every Android accuracy`() {
        assertEquals(SensorAccuracy.UNRELIABLE, SensorManager.SENSOR_STATUS_UNRELIABLE.toSensorAccuracy())
        assertEquals(SensorAccuracy.LOW, SensorManager.SENSOR_STATUS_ACCURACY_LOW.toSensorAccuracy())
        assertEquals(SensorAccuracy.MEDIUM, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM.toSensorAccuracy())
        assertEquals(SensorAccuracy.HIGH, SensorManager.SENSOR_STATUS_ACCURACY_HIGH.toSensorAccuracy())
        assertEquals(SensorAccuracy.UNKNOWN, 999.toSensorAccuracy())
    }
}
