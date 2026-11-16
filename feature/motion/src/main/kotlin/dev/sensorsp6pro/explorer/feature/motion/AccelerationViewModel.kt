package dev.sensorsp6pro.explorer.feature.motion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sensorsp6pro.explorer.core.domain.ObserveAccelerationUseCase
import dev.sensorsp6pro.explorer.core.domain.SamplingRate
import dev.sensorsp6pro.explorer.core.domain.SensorUnavailableException
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AccelerationViewModel @Inject constructor(
    private val observeAcceleration: ObserveAccelerationUseCase,
) : ViewModel() {
    private val active = MutableStateFlow(false)

    val uiState = active.flatMapLatest { isActive ->
        if (!isActive) {
            flowOf(AccelerationUiState(status = MeasurementStatus.PAUSED))
        } else {
            observeAcceleration(SamplingRate.GAME)
                .runningFold(AccelerationUiState(status = MeasurementStatus.STARTING)) { state, sample ->
                    state.copy(
                        status = MeasurementStatus.LIVE,
                        history = (state.history + sample).takeLast(100),
                    )
                }
                .catch { error ->
                    emit(
                        AccelerationUiState(
                            status = if (error is SensorUnavailableException) MeasurementStatus.UNAVAILABLE else MeasurementStatus.ERROR,
                            message = error.message ?: "Unbekannter Sensorfehler",
                        ),
                    )
                }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AccelerationUiState())

    fun setActive(value: Boolean) {
        active.value = value
    }
}
