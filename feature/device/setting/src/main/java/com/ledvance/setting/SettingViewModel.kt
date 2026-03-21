package com.ledvance.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.asMacAddress
import com.ledvance.domain.bean.command.LineSequence
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
) : ViewModel(), SettingContract {

    @AssistedFactory
    interface Factory {
        fun create(deviceId: DeviceId): SettingViewModel
    }

    override val uiState: StateFlow<SettingContract.UiState> = combine(
        flow = getDeviceUseCase(deviceId),
        flow2 = getDeviceStateUseCase(deviceId)
    ) { device, deviceState ->
        SettingContract.UiState.Success(
            isOnline = deviceState?.isOnline ?: false,
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
        viewModelScope.launch {
            deviceControlUseCase.reset(deviceId)
        }
    }

    override fun setLineSequence(lineSequence: LineSequence) {
        viewModelScope.launch {
            deviceControlUseCase.setLineSequence(deviceId, lineSequence)
        }
    }

    override fun upgradeFirmware() {

    }

    override fun onReconnect() {
        viewModelScope.launch {
            deviceControlUseCase.onReconnect(deviceId)
        }
    }

    override fun deleteDevice() {
        viewModelScope.launch {
            deleteDeviceUseCase(deviceId)
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
}