package com.ledvance.ota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.FirmwareVersion
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ota.OtaUpdateContract.UpdateState
import com.ledvance.usecase.device.GetDeviceFirmwareLatestUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import com.ledvance.usecase.device.UpdateFirmwareUseCase
import com.ledvance.usecase.device.UpdateFirmwareUseCase.OtaUpdateResult
import com.ledvance.utils.extensions.sizeString
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : HomeViewModel
 */
@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = OtaUpdateViewModel.Factory::class)
internal class OtaUpdateViewModel @AssistedInject constructor(
    @Assisted private val deviceId: DeviceId,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val getDeviceFirmwareLatestUseCase: GetDeviceFirmwareLatestUseCase,
    private val updateFirmwareUseCase: UpdateFirmwareUseCase,
) : ViewModel(), OtaUpdateContract {
    private val TAG = "OtaUpdateViewModel"

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): OtaUpdateViewModel
    }

    private val screenState = MutableStateFlow(ScreenState())
    override val uiState: StateFlow<OtaUpdateContract.UiState> = combine(
        flow = getDeviceUseCase(deviceId),
        flow2 = getDeviceFirmwareLatestUseCase(deviceId),
        flow3 = screenState,
    ) { device, firmwareLatest, state ->
        OtaUpdateContract.UiState.Success(
            deviceName = device.name,
            currentVersion = device.firmwareVersion,
            latestVersion = firmwareLatest?.latestVersion ?: FirmwareVersion.default,
            otaFilePath = firmwareLatest?.firmwareFilePath ?: "",
            otaFileSize = firmwareLatest?.firmwareFileSize?.sizeString() ?: "",
            updateState = state.updateState
        )
    }.onStart {
        Timber.tag(TAG).d("OtaUpdate -> start loading (deviceId=$deviceId)")
    }.onEach { uiState ->
        Timber.tag(TAG).d("OtaUpdate -> state updated: $uiState")
    }.catch { error ->
        Timber.tag(TAG).e(error, "OtaUpdate -> failed to load")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = OtaUpdateContract.UiState.Loading
    )

    override fun startUpdateFirmware() {
        viewModelScope.launch {
            screenState.update { it.copy(updateState = UpdateState.Progress(0)) }
            val state = (uiState.value as? OtaUpdateContract.UiState.Success)
            val otaFile = File(state?.otaFilePath ?: "")
            if (state == null || !otaFile.exists()) {
                delay(1000)
                screenState.update { it.copy(updateState = UpdateState.Failed) }
                return@launch
            }
            updateFirmwareUseCase(
                parameter = UpdateFirmwareUseCase.Param(
                    deviceId = deviceId,
                    firmwareFile = otaFile,
                    firmwareVersion = state.latestVersion,
                    deviceName = state.deviceName
                )
            ).catch { e ->
                Timber.tag(TAG).e(e, "OTA update flow error")
                screenState.update { it.copy(updateState = UpdateState.Failed) }
            }.collect { result ->
                when (result) {
                    is OtaUpdateResult.Progress -> {
                        screenState.update { it.copy(updateState = UpdateState.Progress(result.progress)) }
                    }

                    is OtaUpdateResult.Fail -> {
                        screenState.update { it.copy(updateState = UpdateState.Failed) }
                    }

                    OtaUpdateResult.Success -> {
                        screenState.update { it.copy(updateState = UpdateState.Success) }
                    }
                }
            }
        }
    }

    override fun isOtaUpdating(): Boolean {
        val state = (uiState.value as? OtaUpdateContract.UiState.Success) ?: return false
        return state.updateState is UpdateState.Progress
    }

    private data class ScreenState(
        val updateState: UpdateState = UpdateState.Idle,
    )
}