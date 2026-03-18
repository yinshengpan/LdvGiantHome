package com.ledvance.home

import androidx.compose.runtime.Immutable
import com.ledvance.ble.bean.ScannedDevice
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : SearchContract
 */
internal interface SearchContract {
    @Immutable
    sealed interface UiState {

        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val devices: List<ScannedDevice>
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun startBleScan()

    fun stopBleScan()
}