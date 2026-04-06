package com.ledvance.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.command.common.TimerType
import com.ledvance.ui.R
import com.ledvance.ui.component.SnackbarManager
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceTimersUseCase
import com.ledvance.usecase.device.SyncDeviceTimerUseCase
import com.ledvance.usecase.device.UpdateDeviceTimerUseCase
import com.ledvance.utils.extensions.getString
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
        TimerContract.UiState.Success(
            timerList = timers.ifEmpty { defaultGiantTimers() }.sortedByDescending { it.timerType.command },
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

    private fun defaultGiantTimers() = listOf(
        TimerUiItem(timerType = TimerType.GiantOn, displayRepeat = getString(R.string.never)),
        TimerUiItem(timerType = TimerType.GiantOff, displayRepeat = getString(R.string.never))
    )

    override fun onTimerChange(timer: TimerUiItem) {
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = updateDeviceTimerUseCase(
                parameter = UpdateDeviceTimerUseCase.Param(
                    deviceId = deviceId,
                    timer = timer
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
