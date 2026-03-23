package com.ledvance.light.screen.scenes

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.command.scenes.Scene
import com.ledvance.light.bean.SceneSegment
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : ScenesContract
 */
internal interface ScenesContract {
    @Immutable
    sealed interface UiState {
        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val isOnline: Boolean,
            val speed: Int,
            val brightness: Int,
            val selectedSceneSegment: SceneSegment,
            val sceneSegments: List<SceneSegment>,
            val selectedScene: Scene?,
            val scenes: List<Scene>,
            val loading: Boolean,
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun onSceneChange(scene: Scene)

    fun onSceneSegmentChange(sceneSegment: SceneSegment)
    fun onSpeedChange(speed: Int)
    fun onBrightnessChange(brightness: Int)
    fun onReconnect()
}
