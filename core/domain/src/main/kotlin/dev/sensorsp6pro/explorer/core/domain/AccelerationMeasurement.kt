package dev.sensorsp6pro.explorer.core.domain

data class Vector3(val x: Float, val y: Float, val z: Float)

enum class SensorAccuracy { UNRELIABLE, LOW, MEDIUM, HIGH, UNKNOWN }

data class AccelerationMeasurement(
    val vector: Vector3,
    val timestampNanos: Long,
    val accuracy: SensorAccuracy,
)

enum class SamplingRate { UI, GAME, FASTEST }

class SensorUnavailableException : IllegalStateException("Beschleunigungssensor nicht verfügbar")
class SensorStartException : IllegalStateException("Beschleunigungssensor konnte nicht gestartet werden")
