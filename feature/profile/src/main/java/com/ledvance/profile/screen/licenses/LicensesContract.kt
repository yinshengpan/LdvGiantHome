package com.ledvance.profile.screen.licenses

import com.ledvance.domain.bean.License

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : LicensesContract
 */
internal interface LicensesContract {
    @Immutable
    sealed interface UiState {
        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val licenses: List<License>
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun onRefresh()
}
