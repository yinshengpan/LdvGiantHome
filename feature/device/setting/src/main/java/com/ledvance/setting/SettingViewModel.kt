package com.ledvance.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.FirmwareVersion
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.command.giant.LineSequence
import com.ledvance.ui.component.SnackbarManager
import com.ledvance.ui.utils.OneTimeActionPublisherContract
import com.ledvance.ui.utils.createDefaultMutableActionFlow
import com.ledvance.usecase.device.DeleteDeviceUseCase
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceFirmwareLatestUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import com.ledvance.usecase.device.SyncFirmwareLatestUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : SettingViewModel
 */
@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = SettingViewModel.Factory::class)
internal class SettingViewModel @AssistedInject constructor(
    @Assisted private val deviceId: DeviceId,
    private val getDeviceStateUseCase: GetDeviceStateUseCase,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val deviceControlUseCase: DeviceControlUseCase,
    private val getDeviceFirmwareLatestUseCase: GetDeviceFirmwareLatestUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase,
    private val syncFirmwareLatestUseCase: SyncFirmwareLatestUseCase,
) : ViewModel(), SettingContract,
    OneTimeActionPublisherContract<SettingContract.SettingOneTimeAction> {

    companion object {
        private const val TAG = "SettingViewModel"
    }

    override val mutableActionFlow: MutableSharedFlow<SettingContract.SettingOneTimeAction> =
        createDefaultMutableActionFlow()

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): SettingViewModel
    }

    private val screenState = MutableStateFlow(ScreenState())

    override val uiState: StateFlow<SettingContract.UiState> = combine(
        flow = getDeviceUseCase(deviceId),
        flow2 = getDeviceStateUseCase(deviceId),
        flow3 = screenState,
        flow4 = getDeviceFirmwareLatestUseCase(deviceId),
    ) { device, deviceState, state, firmwareLatest ->
        SettingContract.UiState.Success(
            isOnline = deviceState.isOnline,
            deviceId = deviceId,
            loading = state.loading,
            deviceName = device.name,
            deviceMacAddress = deviceId.macAddress,
            deviceTypeName = device.deviceType.getDisplayName(),
            deviceIconResId = device.deviceType.getIconResId(),
            lineSequence = device.lineSequence,
            latestFirmwareVersion = firmwareLatest?.latestVersion ?: FirmwareVersion.default,
            firmwareVersion = device.firmwareVersion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SettingContract.UiState.Loading
    )

    init {
        viewModelScope.launch {
            syncFirmwareLatestUseCase()
        }
    }

    override fun resetDevice() {
        Timber.tag(TAG).d("resetDevice: deviceId=%s", deviceId)
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.reset(deviceId)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    override fun setLineSequence(lineSequence: LineSequence) {
        Timber.tag(TAG).d("setLineSequence: deviceId=%s, lineSequence=%s", deviceId, lineSequence)
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.setLineSequence(deviceId, lineSequence)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    override fun onReconnect() {
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.onReconnect(deviceId)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    override fun deleteDevice() {
        Timber.tag(TAG).d("deleteDevice: deviceId=%s", deviceId)
        viewModelScope.launch {
            deleteDeviceUseCase(deviceId)
            publish(SettingContract.SettingOneTimeAction.DeleteSuccess)
        }
    }

    fun DeviceType.getDisplayName(): String {
        return when (this) {
            DeviceType.GiantTable -> "Table lamp"
            DeviceType.GiantFloor -> "Floor lamp"
            DeviceType.LdvBedside -> "Bedside lamp"
        }
    }

    fun DeviceType.getIconResId(): Int {
        return when (this) {
            DeviceType.GiantTable -> com.ledvance.ui.R.mipmap.pic_giant_table
            DeviceType.GiantFloor -> com.ledvance.ui.R.mipmap.pic_giant_floor
            DeviceType.LdvBedside -> com.ledvance.ui.R.mipmap.pic_ldv_bedside
        }
    }

    private data class ScreenState(
        val loading: Boolean = false,
    )
}