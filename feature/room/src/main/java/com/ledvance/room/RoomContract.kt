package com.ledvance.room

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : RoomContract
 */
internal interface RoomContract {
    @Immutable
    sealed interface UiState {

        @Immutable
        data object Success : UiState
    }

    val uiState: StateFlow<UiState>
}