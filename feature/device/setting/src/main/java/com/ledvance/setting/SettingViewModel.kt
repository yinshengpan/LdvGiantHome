package com.ledvance.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.asMacAddress
import com.ledvance.domain.bean.command.LineSequence
import com.ledvance.ui.component.SnackbarManager
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import com.ledvance.vivares.directeasy.core.ui.util.OneTimeActionPublisherContract
import com.ledvance.vivares.directeasy.core.ui.util.createDefaultMutableActionFlow
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
    private val deleteDeviceUseCase: com.ledvance.usecase.device.DeleteDeviceUseCase,
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
    ) { device, deviceState, state ->
        SettingContract.UiState.Success(
            isOnline = deviceState.isOnline,
            loading = state.loading,
            deviceName = device.name,
            deviceMacAddress = deviceId.asMacAddress(),
            deviceTypeName = device.deviceType.getDisplayName(),
            deviceIconResId = device.deviceType.getIconResId(),
            lineSequence = device.lineSequence,
            latestFirmwareVersion = "",
            firmwareVersion = ""
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SettingContract.UiState.Loading
    )

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

    override fun upgradeFirmware() {

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
            DeviceType.Table -> "Table lamp"
            DeviceType.Floor -> "Floor lamp"
        }
    }

    fun DeviceType.getIconResId(): Int {
        return when (this) {
            DeviceType.Table -> com.ledvance.ui.R.mipmap.pic_tablelamp
            DeviceType.Floor -> com.ledvance.ui.R.mipmap.pic_floorlamp
        }
    }

    private data class ScreenState(
        val loading: Boolean = false,
    )
}