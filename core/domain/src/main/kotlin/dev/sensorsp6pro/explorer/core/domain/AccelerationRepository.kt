package dev.sensorsp6pro.explorer.core.domain

import kotlinx.coroutines.flow.Flow

interface AccelerationRepository {
    fun observe(rate: SamplingRate): Flow<AccelerationMeasurement>
}
