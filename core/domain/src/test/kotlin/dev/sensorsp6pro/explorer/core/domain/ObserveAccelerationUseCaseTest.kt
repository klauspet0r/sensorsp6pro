package dev.sensorsp6pro.explorer.core.domain

import app.cash.turbine.test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveAccelerationUseCaseTest {
    @Test
    fun `forwards requested rate and measurements`() = runTest {
        val sample = AccelerationMeasurement(Vector3(0.1f, 0.2f, 9.81f), 42L, SensorAccuracy.HIGH)
        val repository = FakeAccelerationRepository(flowOf(sample))

        ObserveAccelerationUseCase(repository)(SamplingRate.GAME).test {
            assertEquals(sample, awaitItem())
            awaitComplete()
        }
        assertEquals(SamplingRate.GAME, repository.requestedRate)
    }
}
