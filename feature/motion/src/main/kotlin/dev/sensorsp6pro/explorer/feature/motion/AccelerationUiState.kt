package dev.sensorsp6pro.explorer.feature.motion

import dev.sensorsp6pro.explorer.core.domain.AccelerationMeasurement

enum class MeasurementStatus { STARTING, LIVE, PAUSED, UNAVAILABLE, ERROR }

data class AccelerationUiState(
    val status: MeasurementStatus = MeasurementStatus.PAUSED,
    val history: List<AccelerationMeasurement> = emptyList(),
    val message: String? = null,
)
