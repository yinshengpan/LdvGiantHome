package com.ledvance.home

import androidx.compose.runtime.Immutable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : HomeContract
 */
internal interface HomeContract {
    @Immutable
    sealed interface UiState {

        @Immutable
        data object Loading : UiState

        @Immutable
        data object Success : UiState

        @Immutable
        data object Error : UiState
    }
}