package com.ledvance.search

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.usecase.device.AddDeviceUseCase
import com.ledvance.usecase.device.BleSearchUseCase
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetAllDeviceIdUseCase
import com.ledvance.utils.extensions.tryCatch
import com.ledvance.ui.utils.OneTimeActionPublisherContract
import com.ledvance.ui.utils.createDefaultMutableActionFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : SearchViewModel
 */
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val bleSearchUseCase: BleSearchUseCase,
    private val addDeviceUseCase: AddDeviceUseCase,
    private val getAllDeviceIdUseCase: GetAllDeviceIdUseCase,
    private val deviceControlUseCase: DeviceControlUseCase,
) : ViewModel(), SearchContract,
    OneTimeActionPublisherContract<SearchContract.SearchOneTimeAction> {

    override val mutableActionFlow: MutableSharedFlow<SearchContract.SearchOneTimeAction> =
        createDefaultMutableActionFlow()

    private val screenState = MutableStateFlow(ScreenState())

    override val uiState: StateFlow<SearchContract.UiState.Success> = combine(
        flow = bleSearchUseCase.scanDeviceListFlow,
        flow2 = getAllDeviceIdUseCase(),
        flow3 = screenState,
    ) { scanDevices, localDeviceIdList, state ->
        val devices = scanDevices.filter { !localDeviceIdList.contains(it.deviceId) }
        SearchContract.UiState.Success(devices = devices, loading = state.loading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SearchContract.UiState.Success()
    )

    @SuppressLint("MissingPermission")
    override fun startBleScan() {
        tryCatch {
            bleSearchUseCase.startScan()
        }
    }

    override fun stopBleScan() {
        bleSearchUseCase.stopBleScan()
    }

    override fun addDevice(scannedDevice: ScannedDevice) {
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            deviceControlUseCase.connectDevice(scannedDevice.deviceId)
            addDeviceUseCase(
                parameter = AddDeviceUseCase.Param(
                    deviceId = scannedDevice.deviceId,
                    name = scannedDevice.name
                )
            )
            screenState.update { it.copy(loading = false) }
            publish(SearchContract.SearchOneTimeAction.AddDeviceSuccess(scannedDevice.deviceId))
        }
    }

    override fun onCleared() {
        super.onCleared()
        bleSearchUseCase.stopBleScan()
    }

    private data class ScreenState(
        val loading: Boolean = false,
    )
}