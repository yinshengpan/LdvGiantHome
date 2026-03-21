package com.ledvance.light

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.scenes.Scene
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceTimersUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import com.ledvance.usecase.device.SyncDeviceFirmwareUseCase
import com.ledvance.usecase.device.SyncDeviceTimerUseCase
import com.ledvance.usecase.device.UpdateDeviceTimerUseCase
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
import java.time.DayOfWeek

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
    private val getDeviceTimersUseCase: GetDeviceTimersUseCase,
    private val updateDeviceTimerUseCase: UpdateDeviceTimerUseCase,
    private val syncDeviceTimerUseCase: SyncDeviceTimerUseCase,
    private val syncDeviceFirmwareUseCase: SyncDeviceFirmwareUseCase,
) : ViewModel(), LightDetailsContract {

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): LightDetailsViewModel
    }

    private val screenState = MutableStateFlow(ScreenState())
    private val commandFlow = MutableStateFlow<Command?>(null)
    override val uiState: StateFlow<LightDetailsContract.UiState> = combine(
        flow = getDeviceUseCase(deviceId),
        flow2 = getDeviceStateUseCase(deviceId),
        flow3 = getDeviceTimersUseCase(deviceId),
        flow4 = screenState
    ) { device, deviceState, timers, screenState ->
        val onTimer = timers.find { it.timerType == TimerType.ON } ?: TimerUiItem(TimerType.ON)
        val offTimer = timers.find { it.timerType == TimerType.OFF } ?: TimerUiItem(TimerType.OFF)
        LightDetailsContract.UiState.Success(
            deviceName = device.name,
            deviceType = device.deviceType,
            isOnline = deviceState?.isOnline ?: false,
            power = device.power,
            workMode = screenState.workMode ?: device.workMode,
            colourModeHue = screenState.colourModeHue.takeIf { it != -1 } ?: device.h,
            colourModeSat = screenState.colourModeSat.takeIf { it != -1 } ?: device.s,
            colourModeBrightness = screenState.colourModeBrightness.takeIf { it != -1 } ?: device.v,
            whiteModeCct = screenState.whiteModeCct.takeIf { it != -1 } ?: device.cct,
            whiteModeBrightness = screenState.whiteModeBrightness.takeIf { it != -1 } ?: device.brightness,
            speed = screenState.speed.takeIf { it != -1 } ?: device.speed,
            onTimer = onTimer,
            offTimer = offTimer,
            modeType = device.modeType,
            modeId = device.modeId,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LightDetailsContract.UiState.Loading
    )

    init {
        syncDeviceTimerUseCase(
            parameter = SyncDeviceTimerUseCase.Param(
                deviceId = deviceId,
                scope = viewModelScope
            )
        )
        viewModelScope.launch {
            deviceControlUseCase.queryDeviceInfo(deviceId)
            deviceControlUseCase.syncDeviceTime(deviceId)
            deviceControlUseCase.queryTimer(deviceId)
            syncDeviceFirmwareUseCase(deviceId)
        }
        viewModelScope.launch {
            commandFlow.sample(300).collectLatest {
                it ?: return@collectLatest
                when (it) {
                    is Command.ColourModeHs -> {
                        deviceControlUseCase.setColourModeHS(deviceId, it.hue, it.sat)
                    }

                    is Command.ColourModeBrightness -> {
                        deviceControlUseCase.setColourModeBrightness(deviceId, it.brightness)
                    }

                    is Command.WhiteModeCct -> {
                        deviceControlUseCase.setWhiteModeCCT(deviceId, it.cct)
                    }

                    is Command.WhiteModeBrightness -> {
                        deviceControlUseCase.setWhiteModeBrightness(deviceId, it.brightness)
                    }

                    is Command.Speed -> {
                        deviceControlUseCase.setSpeed(deviceId, it.speed)
                    }

                    is Command.Mode -> {
                        deviceControlUseCase.setMode(deviceId, it.modeId)
                    }
                }
            }
        }
    }

    override fun onSwitchChange(switch: Boolean) {
        viewModelScope.launch {
            deviceControlUseCase.setPower(deviceId, switch)
        }
    }

    override fun onWorkModeChange(workMode: WorkMode) {
        val state = screenState.value
        when (workMode) {
            WorkMode.White -> {
                commandFlow.tryEmit(Command.WhiteModeCct(state.whiteModeCct))
            }

            WorkMode.Colour -> {
                commandFlow.tryEmit(Command.ColourModeHs(state.colourModeHue, state.colourModeSat))
            }

            else -> {}
        }
        screenState.update { it.copy(workMode = workMode) }
    }

    override fun onColourModeHsChange(hue: Int, sat: Int) {
        screenState.update { it.copy(colourModeHue = hue, colourModeSat = sat) }
        commandFlow.tryEmit(Command.ColourModeHs(hue, sat))
    }


    override fun onColourModeBrightnessChange(brightness: Int) {
        screenState.update { it.copy(colourModeBrightness = brightness) }
        commandFlow.tryEmit(Command.ColourModeBrightness(brightness))
    }

    override fun onWhiteModeCctChange(cct: Int) {
        screenState.update { it.copy(whiteModeCct = cct) }
        commandFlow.tryEmit(Command.WhiteModeCct(cct))
    }

    override fun onWhiteModeBrightnessChange(brightness: Int) {
        screenState.update { it.copy(whiteModeBrightness = brightness) }
        commandFlow.tryEmit(Command.WhiteModeBrightness(brightness))
    }

    override fun onSceneChange(scene: Scene) {
        viewModelScope.launch {
            deviceControlUseCase.setScene(deviceId, scene)
        }
    }

    override fun onSpeedChange(speed: Int) {
        screenState.update { it.copy(speed = speed) }
        commandFlow.tryEmit(Command.Speed(speed))
    }

    override fun onModeIdChange(modeId: ModeId) {
        commandFlow.tryEmit(Command.Mode(modeId))
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
        val currentState = uiState.value as? LightDetailsContract.UiState.Success ?: return
        val timer = (if (timerType == TimerType.ON) currentState.onTimer else currentState.offTimer)
        viewModelScope.launch {
            updateDeviceTimerUseCase(
                parameter = UpdateDeviceTimerUseCase.Param(
                    deviceId = deviceId,
                    timer = transform(timer)
                )
            )
        }
    }

    private data class ScreenState(
        val workMode: WorkMode? = null,
        val colourModeHue: Int = -1,
        val colourModeSat: Int = -1,
        val colourModeBrightness: Int = -1,
        val whiteModeCct: Int = -1,
        val whiteModeBrightness: Int = -1,
        val speed: Int = -1,
    )

    sealed interface Command {
        data class ColourModeHs(val hue: Int, val sat: Int) : Command
        data class ColourModeBrightness(val brightness: Int) : Command
        data class WhiteModeCct(val cct: Int) : Command
        data class WhiteModeBrightness(val brightness: Int) : Command
        data class Speed(val speed: Int) : Command
        data class Mode(val modeId: ModeId) : Command
    }
}