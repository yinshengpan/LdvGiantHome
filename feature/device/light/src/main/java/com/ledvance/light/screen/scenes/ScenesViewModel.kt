package com.ledvance.light.screen.scenes

import SnackbarManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.command.scenes.FloorScenes
import com.ledvance.domain.bean.command.scenes.Scene
import com.ledvance.domain.bean.command.scenes.TableScenes
import com.ledvance.light.bean.LightCommand
import com.ledvance.light.bean.SceneSegment
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
        ScenesContract.UiState.Success(
            isOnline = deviceState.isOnline,
            speed = state.speed.takeIf { it != -1 } ?: device.speed,
            selectedSceneSegment = state.selectedSceneSegment,
            scenes = device.deviceType.getScenes(state.selectedSceneSegment),
            sceneSegments = device.deviceType.getSceneSceneSegmentList(),
            commandLoading = state.commandLoading
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

                    else -> {}
                }
            }
        }
    }

    override fun onSceneChange(scene: Scene) {
        viewModelScope.launch {
            screenState.update { it.copy(commandLoading = true) }
            val success = deviceControlUseCase.setScene(deviceId, scene)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(commandLoading = false) }
        }
    }

    override fun onSceneSegmentChange(sceneSegment: SceneSegment) {
        screenState.update { it.copy(selectedSceneSegment = sceneSegment) }
    }

    override fun onSpeedChange(speed: Int) {
        screenState.update { it.copy(speed = speed) }
        lightCommandFlow.tryEmit(LightCommand.Speed(speed))
    }

    override fun onReconnect() {
        viewModelScope.launch {
            screenState.update { it.copy(commandLoading = true) }
            val success = deviceControlUseCase.onReconnect(deviceId)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(commandLoading = false) }
        }
    }

    private fun DeviceType.getScenes(sceneSegment: SceneSegment? = null): List<Scene> {
        return when (this) {
            DeviceType.Table -> when (sceneSegment) {
                null -> listOf()
                else -> TableScenes.tableScenes[sceneSegment.value] ?: listOf()
            }

            DeviceType.Floor -> FloorScenes.getAllScene()
        }
    }

    private fun DeviceType.getSceneSceneSegmentList(): List<SceneSegment> {
        return when (this) {
            DeviceType.Table -> SceneSegment.allSceneSegment
            DeviceType.Floor -> listOf()
        }
    }

    private data class ScreenState(
        val selectedSceneSegment: SceneSegment = SceneSegment.Natural,
        val sceneSegments: List<SceneSegment> = listOf(),
        val scenes: List<Scene> = listOf(),
        val speed: Int = -1,
        val commandLoading: Boolean = false,
    )
}
