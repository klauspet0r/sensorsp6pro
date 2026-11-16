package dev.sensorsp6pro.explorer.data.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dev.sensorsp6pro.explorer.core.domain.AccelerationMeasurement
import dev.sensorsp6pro.explorer.core.domain.AccelerationRepository
import dev.sensorsp6pro.explorer.core.domain.SamplingRate
import dev.sensorsp6pro.explorer.core.domain.SensorStartException
import dev.sensorsp6pro.explorer.core.domain.SensorUnavailableException
import dev.sensorsp6pro.explorer.core.domain.Vector3
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SensorManagerAccelerationRepository @Inject constructor(
    private val sensorManager: SensorManager,
) : AccelerationRepository {
    override fun observe(rate: SamplingRate): Flow<AccelerationMeasurement> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensor == null) {
            close(SensorUnavailableException())
            return@callbackFlow
        }
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(
                    AccelerationMeasurement(
                        vector = Vector3(event.values[0], event.values[1], event.values[2]),
                        timestampNanos = event.timestamp,
                        accuracy = event.accuracy.toSensorAccuracy(),
                    ),
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        val delay = when (rate) {
            SamplingRate.UI -> SensorManager.SENSOR_DELAY_UI
            SamplingRate.GAME -> SensorManager.SENSOR_DELAY_GAME
            SamplingRate.FASTEST -> SensorManager.SENSOR_DELAY_FASTEST
        }
        if (!sensorManager.registerListener(listener, sensor, delay)) {
            sensorManager.unregisterListener(listener)
            close(SensorStartException())
            return@callbackFlow
        }
        awaitClose { sensorManager.unregisterListener(listener) }
    }
}
