package com.ledvance.mode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.LightCommand
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.ui.component.SnackbarManager
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : ModeViewModel
 */
@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = ModeViewModel.Factory::class)
internal class ModeViewModel @AssistedInject constructor(
    @Assisted private val deviceId: DeviceId,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val getDeviceStateUseCase: GetDeviceStateUseCase,
    private val deviceControlUseCase: DeviceControlUseCase
) : ViewModel(), ModeContract {

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): ModeViewModel
    }

    private val lightCommandFlow = MutableStateFlow<LightCommand?>(null)
    private val screenState = MutableStateFlow(ScreenState())

    override val uiState: StateFlow<ModeContract.UiState> = combine(
        flow = getDeviceUseCase(deviceId),
        flow2 = getDeviceStateUseCase(deviceId),
        flow3 = screenState
    ) { device, deviceState, state ->
        ModeContract.UiState.Success(
            modeId = device.modeId,
            isOnline = deviceState.isOnline,
            brightness = state.brightness.takeIf { it != -1 } ?: device.v,
            speed = state.speed.takeIf { it != -1 } ?: device.speed,
            loading = state.loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ModeContract.UiState.Loading
    )


    init {
        viewModelScope.launch {
            lightCommandFlow.sample(300).collectLatest {
                it ?: return@collectLatest
                when (it) {
                    is LightCommand.Speed -> {
                        deviceControlUseCase.setSpeed(deviceId, it.speed)
                    }

                    is LightCommand.ColourModeBrightness -> {
                        deviceControlUseCase.setColourModeBrightness(deviceId, it.brightness)
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onModeIdChange(modeId: ModeId) {
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.setMode(deviceId, modeId)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    override fun onBrightnessChange(brightness: Int) {
        screenState.update { it.copy(brightness = brightness) }
        lightCommandFlow.tryEmit(LightCommand.ColourModeBrightness(brightness))
    }

    override fun onSpeedChange(speed: Int) {
        screenState.update { it.copy(speed = speed) }
        lightCommandFlow.tryEmit(LightCommand.Speed(speed))
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

    private data class ScreenState(
        val loading: Boolean = false,
        val brightness: Int = -1,
        val speed: Int = -1,
    )
}
