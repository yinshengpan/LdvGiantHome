package com.ledvance.light

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.common.ModeType
import com.ledvance.light.component.CardFeature
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : LightDetailsContract
 */
internal interface LightDetailsContract {

    @Immutable
    sealed interface UiState {

        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val loading: Boolean = false,
            val isOnline: Boolean,
            val deviceName: String,
            val deviceType: DeviceType,
            val detailState: DetailState,
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    @Immutable
    sealed interface DetailState {
        data class GiantDetailState(
            val power: Boolean,
            val workMode: WorkMode,
            val colourModeHue: Int,
            val colourModeSat: Int,
            val colourModeBrightness: Int,
            val whiteModeCct: Int,
            val whiteModeBrightness: Int,
            val cardFeatureList: List<CardFeature> = listOf(),
        ) : DetailState

        data class LdvBedsideState(
            val power: Boolean,
            val brightness: Int,
            val cct: Int,
            val modeType: ModeType,
            val modeList: List<ModeType>,
            val timerList: List<TimerUiItem> = listOf(),
        ) : DetailState
    }

    val uiState: StateFlow<UiState>

    fun onSwitchChange(switch: Boolean)

    fun onWorkModeChange(workMode: WorkMode)

    fun onColourModeHsChange(hue: Int, sat: Int)
    fun onColourModeBrightnessChange(brightness: Int)

    fun onWhiteModeCctChange(cct: Int)
    fun onWhiteModeBrightnessChange(brightness: Int)

    fun onModeChange(modeType: ModeType)
    fun onTimerChange(timer: TimerUiItem)

    fun onReconnect()
}