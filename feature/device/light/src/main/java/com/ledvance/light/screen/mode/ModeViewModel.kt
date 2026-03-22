package com.ledvance.light.screen.mode

import SnackbarManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : ModeViewModel
 */
@HiltViewModel(assistedFactory = ModeViewModel.Factory::class)
internal class ModeViewModel @AssistedInject constructor(
    @Assisted private val deviceId: DeviceId,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val deviceControlUseCase: DeviceControlUseCase
) : ViewModel(), ModeContract {

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): ModeViewModel
    }

    private val screenState = MutableStateFlow(ScreenState())

    override val uiState: StateFlow<ModeContract.UiState> = combine(
        getDeviceUseCase(deviceId),
        screenState
    ) { device, state ->
        ModeContract.UiState.Success(
            modeId = device.modeId,
            commandLoading = state.commandLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ModeContract.UiState.Loading
    )

    override fun onModeIdChange(modeId: ModeId) {
        viewModelScope.launch {
            screenState.update { it.copy(commandLoading = true) }
            val success = deviceControlUseCase.setMode(deviceId, modeId)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(commandLoading = false) }
        }
    }

    private data class ScreenState(val commandLoading: Boolean = false)
}
