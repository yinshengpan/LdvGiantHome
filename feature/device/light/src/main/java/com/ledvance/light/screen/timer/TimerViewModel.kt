package com.ledvance.light.screen.timer

import com.ledvance.ui.component.SnackbarManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceTimersUseCase
import com.ledvance.usecase.device.SyncDeviceTimerUseCase
import com.ledvance.usecase.device.UpdateDeviceTimerUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : TimerViewModel
 */
@HiltViewModel(assistedFactory = TimerViewModel.Factory::class)
internal class TimerViewModel @AssistedInject constructor(
    @Assisted private val deviceId: DeviceId,
    private val getDeviceTimersUseCase: GetDeviceTimersUseCase,
    private val updateDeviceTimerUseCase: UpdateDeviceTimerUseCase,
    private val getDeviceStateUseCase: GetDeviceStateUseCase,
    private val syncDeviceTimerUseCase: SyncDeviceTimerUseCase,
    private val deviceControlUseCase: DeviceControlUseCase
) : ViewModel(), TimerContract {

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): TimerViewModel
    }

    private val screenState = MutableStateFlow(ScreenState())

    override val uiState: StateFlow<TimerContract.UiState> = combine(
        flow = getDeviceTimersUseCase(deviceId),
        flow2 = getDeviceStateUseCase(deviceId),
        flow3 = screenState
    ) { timers, deviceState, state ->
        val onTimer = timers.find { it.timerType == TimerType.ON } ?: TimerUiItem(TimerType.ON)
        val offTimer = timers.find { it.timerType == TimerType.OFF } ?: TimerUiItem(TimerType.OFF)
        TimerContract.UiState.Success(
            onTimer = onTimer,
            offTimer = offTimer,
            isOnline = deviceState.isOnline,
            loading = state.loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TimerContract.UiState.Loading
    )

    init {
        syncDeviceTimerUseCase(
            parameter = SyncDeviceTimerUseCase.Param(
                deviceId = deviceId,
                scope = viewModelScope
            )
        )
        viewModelScope.launch {
            deviceControlUseCase.queryTimer(deviceId)
        }
    }

    override fun onTimerSwitchChange(timerType: TimerType, enabled: Boolean) {
        updateTimer(timerType) { it.copy(enabled = enabled) }
    }

    override fun onTimerTimeChange(timerType: TimerType, hour: Int, minute: Int) {
        updateTimer(timerType) { it.copy(hour = hour, minute = minute) }
    }

    override fun onTimerRepeatChange(timerType: TimerType, days: Set<DayOfWeek>) {
        updateTimer(timerType) { it.copy(days = days) }
    }

    private fun updateTimer(timerType: TimerType, transform: (TimerUiItem) -> TimerUiItem) {
        val currentState = uiState.value as? TimerContract.UiState.Success ?: return
        val timer = if (timerType == TimerType.ON) currentState.onTimer else currentState.offTimer
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = updateDeviceTimerUseCase(
                parameter = UpdateDeviceTimerUseCase.Param(
                    deviceId = deviceId,
                    timer = transform(timer)
                )
            )
            if (success.isFailure || !success.getOrDefault(false)) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    override fun onReconnect() {
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.onReconnect(deviceId)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    private data class ScreenState(val loading: Boolean = false)
}
