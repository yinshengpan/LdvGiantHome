package com.ledvance.light

import androidx.compose.runtime.Immutable
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
        data object Success : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>
}