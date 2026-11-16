package dev.sensorsp6pro.explorer.feature.motion

import app.cash.turbine.test
import dev.sensorsp6pro.explorer.core.domain.AccelerationMeasurement
import dev.sensorsp6pro.explorer.core.domain.AccelerationRepository
import dev.sensorsp6pro.explorer.core.domain.ObserveAccelerationUseCase
import dev.sensorsp6pro.explorer.core.domain.SamplingRate
import dev.sensorsp6pro.explorer.core.domain.SensorAccuracy
import dev.sensorsp6pro.explorer.core.domain.Vector3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccelerationViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `active state keeps only latest one hundred samples and pause clears subscription`() = runTest {
        val source = MutableSharedFlow<AccelerationMeasurement>()
        val viewModel = AccelerationViewModel(
            ObserveAccelerationUseCase(FakeAccelerationRepository(source)),
        )
        viewModel.uiState.test {
            assertEquals(MeasurementStatus.PAUSED, awaitItem().status)
            viewModel.setActive(true)
            assertEquals(MeasurementStatus.STARTING, awaitItem().status)
            repeat(101) { index ->
                source.emit(AccelerationMeasurement(Vector3(index.toFloat(), 0f, 9.81f), index.toLong(), SensorAccuracy.HIGH))
            }
            val live = expectMostRecentItem()
            assertEquals(MeasurementStatus.LIVE, live.status)
            assertEquals(100, live.history.size)
            assertEquals(100f, live.history.last().vector.x)
            viewModel.setActive(false)
            assertEquals(MeasurementStatus.PAUSED, awaitItem().status)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private class FakeAccelerationRepository(
    private val measurements: Flow<AccelerationMeasurement>,
) : AccelerationRepository {
    override fun observe(rate: SamplingRate): Flow<AccelerationMeasurement> = measurements
}
