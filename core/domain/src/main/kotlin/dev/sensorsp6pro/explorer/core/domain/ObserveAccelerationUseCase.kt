package dev.sensorsp6pro.explorer.core.domain

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAccelerationUseCase @Inject constructor(
    private val repository: AccelerationRepository,
) {
    operator fun invoke(rate: SamplingRate): Flow<AccelerationMeasurement> = repository.observe(rate)
}
