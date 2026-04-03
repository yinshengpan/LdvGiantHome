package com.ledvance.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.LightCommand
import com.ledvance.domain.bean.command.common.ModeType
import com.ledvance.domain.bean.command.giant.scenes.FloorScenes
import com.ledvance.domain.bean.command.giant.scenes.Scene
import com.ledvance.domain.bean.command.giant.scenes.TableScenes
import com.ledvance.ui.component.SnackbarManager
import com.ledvance.ui.segment.SceneSegment
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
 * Describe : ScenesViewModel
 */
@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = ScenesViewModel.Factory::class)
internal class ScenesViewModel @AssistedInject constructor(
    @Assisted private val deviceId: DeviceId,
    private val getDeviceStateUseCase: GetDeviceStateUseCase,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val deviceControlUseCase: DeviceControlUseCase,
) : ViewModel(), ScenesContract {

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): ScenesViewModel
    }

    private val lightCommandFlow = MutableStateFlow<LightCommand?>(null)
    private val screenState = MutableStateFlow(ScreenState())

    override val uiState: StateFlow<ScenesContract.UiState> = combine(
        flow = getDeviceUseCase(deviceId),
        flow2 = getDeviceStateUseCase(deviceId),
        flow3 = screenState
    ) { device, deviceState, state ->
        val scenes = device.deviceType.getScenes(state.selectedSceneSegment)
        val selectedGiantScene = if (device.modeType == ModeType.GiantScene) {
            scenes.firstOrNull { it.command == device.modeId?.command }
        } else null
        ScenesContract.UiState.Success(
            isOnline = deviceState.isOnline,
            brightness = state.brightness.takeIf { it != -1 } ?: device.v,
            speed = state.speed.takeIf { it != -1 } ?: device.speed,
            selectedSceneSegment = state.selectedSceneSegment,
            selectedScene = selectedGiantScene,
            scenes = device.deviceType.getScenes(state.selectedSceneSegment),
            sceneSegments = device.deviceType.getSceneSceneSegmentList(),
            loading = state.loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ScenesContract.UiState.Loading
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

    override fun onSceneChange(scene: Scene) {
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.setScene(deviceId, scene)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    override fun onSceneSegmentChange(sceneSegment: SceneSegment) {
        screenState.update { it.copy(selectedSceneSegment = sceneSegment) }
    }

    override fun onSpeedChange(speed: Int) {
        screenState.update { it.copy(speed = speed) }
        lightCommandFlow.tryEmit(LightCommand.Speed(speed))
    }

    override fun onBrightnessChange(brightness: Int) {
        screenState.update { it.copy(brightness = brightness) }
        lightCommandFlow.tryEmit(LightCommand.ColourModeBrightness(brightness))
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

    private fun DeviceType.getScenes(sceneSegment: SceneSegment? = null): List<Scene> {
        return when (this) {
            DeviceType.GiantTable -> when (sceneSegment) {
                null -> listOf()
                else -> TableScenes.tableScenes[sceneSegment.value] ?: listOf()
            }

            DeviceType.GiantFloor -> FloorScenes.getAllScene()
            else -> listOf()
        }
    }

    private fun DeviceType.getSceneSceneSegmentList(): List<SceneSegment> {
        return when (this) {
            DeviceType.GiantTable -> SceneSegment.allSceneSegment
            DeviceType.GiantFloor -> listOf()
            else -> listOf()
        }
    }

    private data class ScreenState(
        val selectedSceneSegment: SceneSegment = SceneSegment.Natural,
        val sceneSegments: List<SceneSegment> = listOf(),
        val scenes: List<Scene> = listOf(),
        val brightness: Int = -1,
        val speed: Int = -1,
        val loading: Boolean = false,
    )
}
