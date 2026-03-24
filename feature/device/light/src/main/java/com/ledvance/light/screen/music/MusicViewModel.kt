package com.ledvance.light.screen.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.command.DeviceMicRhythm
import com.ledvance.light.bean.LightCommand
import com.ledvance.light.bean.MusicSegment
import com.ledvance.light.screen.music.fft.AudioLightController
import com.ledvance.ui.component.SnackbarManager
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import com.ledvance.usecase.device.UpdatePhoneMicSensitivityUseCase
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
 * Describe : MusicViewModel
 */
@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = MusicViewModel.Factory::class)
internal class MusicViewModel @AssistedInject constructor(
    @Assisted private val deviceId: DeviceId,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val getDeviceStateUseCase: GetDeviceStateUseCase,
    private val deviceControlUseCase: DeviceControlUseCase,
    private val updatePhoneMicSensitivityUseCase: UpdatePhoneMicSensitivityUseCase,
) : ViewModel(), MusicContract {

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): MusicViewModel
    }

    private val lightCommandFlow = MutableStateFlow<LightCommand?>(null)
    private val screenState = MutableStateFlow(ScreenState())
    override val uiState: StateFlow<MusicContract.UiState> = combine(
        flow = getDeviceUseCase(deviceId),
        flow2 = getDeviceStateUseCase(deviceId),
        flow3 = screenState
    ) { device, deviceState, state ->
        MusicContract.UiState.Success(
            loading = state.loading,
            isOnline = deviceState.isOnline,
            musicSegment = state.selectedMusicSegment,
            musicSegmentList = state.musicSegments,
            deviceMicRhythm = state.selectedDeviceMicRhythm,
            deviceMicRhythmList = DeviceMicRhythm.itemsMap[device.deviceType] ?: listOf(),
            phoneMicSensitivity = device.phoneMicSensitivity,
            deviceMicSensitivity = state.deviceMicSensitivity,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MusicContract.UiState.Loading
    )

    init {
        viewModelScope.launch {
            lightCommandFlow.sample(300).collectLatest {
                it ?: return@collectLatest
                when (it) {
                    is LightCommand.DeviceMicSensitivity -> {
                        deviceControlUseCase.setDeviceMicSensitivity(deviceId, it.sensitivity)
                    }

                    else -> {}
                }
            }
        }

        // Handle Audio Visualizer commands with 50ms throttling (20 FPS)
        viewModelScope.launch {
            AudioLightController.lightFlow.sample(50).collectLatest { data ->
                val (r, g, b, brightness) = data
                // Send RGB and Brightness
                deviceControlUseCase.setRgb(deviceId, r, g, b)
                deviceControlUseCase.setBrightness(deviceId, brightness)
            }
        }
    }

    override fun onDeviceMicSensitivityChange(sensitivity: Int) {
        screenState.update { it.copy(deviceMicSensitivity = sensitivity) }
        lightCommandFlow.tryEmit(LightCommand.DeviceMicSensitivity(sensitivity))
    }

    override fun onPhoneMicSensitivityChange(sensitivity: Int) {
        viewModelScope.launch {
            updatePhoneMicSensitivityUseCase(
                parameter =
                    UpdatePhoneMicSensitivityUseCase.Param(
                        deviceId = deviceId,
                        sensitivity = sensitivity
                    )
            )
        }
    }

    override fun onMusicSegmentChange(musicSegment: MusicSegment) {
        screenState.update { it.copy(selectedMusicSegment = musicSegment) }
    }

    override fun onRhythmChange(deviceMicRhythm: DeviceMicRhythm) {
        screenState.update { it.copy(selectedDeviceMicRhythm = deviceMicRhythm) }
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.setDeviceMicRhythm(deviceId, deviceMicRhythm)
            if (!success) {
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

    private data class ScreenState(
        val selectedMusicSegment: MusicSegment = MusicSegment.DeviceMic,
        val musicSegments: List<MusicSegment> = listOf(
            MusicSegment.DeviceMic,
            MusicSegment.PhoneMic,
            MusicSegment.Music
        ),
        val selectedDeviceMicRhythm: DeviceMicRhythm = DeviceMicRhythm.Energy1,
        val deviceMicSensitivity: Int = 80,
        val loading: Boolean = false,
    )
}
