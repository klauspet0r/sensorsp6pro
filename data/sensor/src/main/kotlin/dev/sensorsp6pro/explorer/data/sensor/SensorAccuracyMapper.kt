package dev.sensorsp6pro.explorer.data.sensor

import android.hardware.SensorManager
import dev.sensorsp6pro.explorer.core.domain.SensorAccuracy

fun Int.toSensorAccuracy(): SensorAccuracy = when (this) {
    SensorManager.SENSOR_STATUS_UNRELIABLE -> SensorAccuracy.UNRELIABLE
    SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracy.LOW
    SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracy.MEDIUM
    SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracy.HIGH
    else -> SensorAccuracy.UNKNOWN
}
