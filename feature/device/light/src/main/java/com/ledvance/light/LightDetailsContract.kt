package com.ledvance.light

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.scenes.Scene
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
            val deviceName: String,
            val switch: Boolean,
            val workMode: WorkMode,
            val colourModeHue: Int,
            val colourModeSat: Int,
            val colourModeBrightness: Int,
            val whiteModeCct: Int,
            val whiteModeBrightness: Int
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun onSwitchChange(switch: Boolean)

    fun onWorkModeChange(workMode: WorkMode)

    fun onColourModeHsChange(hue: Int, sat: Int)
    fun onColourModeBrightnessChange(brightness: Int)

    fun onWhiteModeCctChange(cct: Int)
    fun onWhiteModeBrightnessChange(brightness: Int)
    fun onClickScene(scene: Scene)
}