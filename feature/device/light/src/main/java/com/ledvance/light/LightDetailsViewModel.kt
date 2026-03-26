package com.ledvance.light

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.LightCommand
import com.ledvance.domain.bean.WorkMode
import com.ledvance.light.component.CardFeature
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : LightDetailsViewModel
 */
@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = LightDetailsViewModel.Factory::class)
internal class LightDetailsViewModel @AssistedInject constructor(
    @Assisted private val deviceId: DeviceId,
    private val getDeviceStateUseCase: GetDeviceStateUseCase,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val deviceControlUseCase: DeviceControlUseCase,
) : ViewModel(), LightDetailsContract {
    private val TAG = "LightDetailsViewModel"

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): LightDetailsViewModel
    }

    private val screenState = MutableStateFlow(ScreenState())
    private val lightCommandFlow = MutableStateFlow<LightCommand?>(null)
    private val cardFeatureMap by lazy {
        mapOf(
            DeviceType.GiantTable to listOf(
                CardFeature.Scene,
                CardFeature.Timer,
                CardFeature.Music,
            ),
            DeviceType.GiantFloor to listOf(
                CardFeature.Scene,
                CardFeature.Timer,
                CardFeature.Music,
                CardFeature.Mode,
            )
        )
    }
    override val uiState: StateFlow<LightDetailsContract.UiState> = combine(
        getDeviceUseCase(deviceId),
        getDeviceStateUseCase(deviceId),
        screenState
    ) { device, deviceState, screenState ->
        LightDetailsContract.UiState.Success(
            deviceName = device.name,
            deviceType = device.deviceType,
            isOnline = deviceState.isOnline,
            power = device.power,
            workMode = screenState.workMode ?: device.workMode,
            colourModeHue = screenState.colourModeHue.takeIf { it != -1 } ?: device.h,
            colourModeSat = screenState.colourModeSat.takeIf { it != -1 } ?: device.s,
            colourModeBrightness = screenState.colourModeBrightness.takeIf { it != -1 } ?: device.v,
            whiteModeCct = screenState.whiteModeCct.takeIf { it != -1 } ?: device.cct,
            whiteModeBrightness = screenState.whiteModeBrightness.takeIf { it != -1 } ?: device.brightness,
            cardFeatureList = cardFeatureMap[device.deviceType] ?: listOf() ,
            loading = screenState.loading,
        )
    }.onStart {
        Timber.tag(TAG).d("Detail -> start loading (deviceId=$deviceId)")
    }.onEach { uiState ->
        Timber.tag(TAG).d("Detail -> state updated (deviceId=$deviceId): $uiState")
    }.catch { error ->
        Timber.tag(TAG).e(error, "Detail -> failed to load (deviceId=$deviceId)")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LightDetailsContract.UiState.Loading
    )

    init {
        viewModelScope.launch {
            deviceControlUseCase.queryDeviceInfo(deviceId)
        }
        viewModelScope.launch {
            lightCommandFlow.sample(300).collectLatest {
                it ?: return@collectLatest
                when (it) {
                    is LightCommand.ColourModeHs -> {
                        deviceControlUseCase.setColourModeHS(deviceId, it.hue, it.sat)
                    }

                    is LightCommand.ColourModeBrightness -> {
                        deviceControlUseCase.setColourModeBrightness(deviceId, it.brightness)
                    }

                    is LightCommand.WhiteModeCct -> {
                        deviceControlUseCase.setWhiteModeCCT(deviceId, it.cct)
                    }

                    is LightCommand.WhiteModeBrightness -> {
                        deviceControlUseCase.setWhiteModeBrightness(deviceId, it.brightness)
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onSwitchChange(switch: Boolean) {
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.setPower(deviceId, switch)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    override fun onWorkModeChange(workMode: WorkMode) {
        val state = screenState.value
        when (workMode) {
            WorkMode.White -> {
                lightCommandFlow.tryEmit(LightCommand.WhiteModeCct(state.whiteModeCct))
            }

            WorkMode.Colour -> {
                lightCommandFlow.tryEmit(LightCommand.ColourModeHs(state.colourModeHue, state.colourModeSat))
            }

            else -> {}
        }
        screenState.update { it.copy(workMode = workMode) }
    }

    override fun onColourModeHsChange(hue: Int, sat: Int) {
        screenState.update { it.copy(colourModeHue = hue, colourModeSat = sat) }
        lightCommandFlow.tryEmit(LightCommand.ColourModeHs(hue, sat))
    }


    override fun onColourModeBrightnessChange(brightness: Int) {
        screenState.update { it.copy(colourModeBrightness = brightness) }
        lightCommandFlow.tryEmit(LightCommand.ColourModeBrightness(brightness))
    }

    override fun onWhiteModeCctChange(cct: Int) {
        screenState.update { it.copy(whiteModeCct = cct) }
        lightCommandFlow.tryEmit(LightCommand.WhiteModeCct(cct))
    }

    override fun onWhiteModeBrightnessChange(brightness: Int) {
        screenState.update { it.copy(whiteModeBrightness = brightness) }
        lightCommandFlow.tryEmit(LightCommand.WhiteModeBrightness(brightness))
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
        val workMode: WorkMode? = null,
        val colourModeHue: Int = -1,
        val colourModeSat: Int = -1,
        val colourModeBrightness: Int = -1,
        val whiteModeCct: Int = -1,
        val whiteModeBrightness: Int = -1,
        val loading: Boolean = false,
        val cardList: List<CardFeature> = listOf(
            CardFeature.Scene,
            CardFeature.Timer,
            CardFeature.Music,
            CardFeature.Mode,
        )
    )
}