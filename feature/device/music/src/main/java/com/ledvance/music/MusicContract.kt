package com.ledvance.music

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.command.giant.DeviceMicRhythm
import com.ledvance.ui.segment.MusicSegment
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : MusicContract
 */
internal interface MusicContract {
    @Immutable
    sealed interface UiState {
        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val loading: Boolean,
            val isOnline: Boolean,
            val musicSegment: MusicSegment,
            val musicSegmentList: List<MusicSegment>,
            val deviceMicRhythm: DeviceMicRhythm,
            val deviceMicRhythmList: List<DeviceMicRhythm>,
            val phoneMicSensitivity: Int,
            val deviceMicSensitivity: Int,
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun onRhythmChange(deviceMicRhythm: DeviceMicRhythm)
    fun onDeviceMicSensitivityChange(sensitivity: Int)
    fun onPhoneMicSensitivityChange(sensitivity: Int)
    fun onMusicSegmentChange(musicSegment: MusicSegment)
    fun onReconnect()
}
