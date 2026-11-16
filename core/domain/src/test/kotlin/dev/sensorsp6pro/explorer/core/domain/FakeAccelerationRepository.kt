package dev.sensorsp6pro.explorer.core.domain

import kotlinx.coroutines.flow.Flow

class FakeAccelerationRepository(
    private val measurements: Flow<AccelerationMeasurement>,
) : AccelerationRepository {
    var requestedRate: SamplingRate? = null

    override fun observe(rate: SamplingRate): Flow<AccelerationMeasurement> {
        requestedRate = rate
        return measurements
    }
}
