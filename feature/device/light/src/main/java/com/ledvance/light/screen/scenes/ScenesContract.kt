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
            val selectedSceneSegment: SceneSegment = SceneSegment.Natural,
            val sceneSegments: List<SceneSegment> = listOf(),
            val scenes: List<Scene> = listOf(),
            val commandLoading: Boolean = false
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun onSceneChange(scene: Scene)

    fun onSceneSegmentChange(sceneSegment: SceneSegment)
    fun onSpeedChange(speed: Int)
    fun onReconnect()
}
