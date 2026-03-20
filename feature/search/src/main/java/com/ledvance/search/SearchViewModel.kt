package com.ledvance.search

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.usecase.device.AddDeviceUseCase
import com.ledvance.usecase.device.BleSearchUseCase
import com.ledvance.usecase.device.GetAllDeviceIdUseCase
import com.ledvance.utils.extensions.tryCatch
import com.ledvance.vivares.directeasy.core.ui.util.OneTimeActionPublisherContract
import com.ledvance.vivares.directeasy.core.ui.util.createDefaultMutableActionFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
) : ViewModel(), SearchContract,
    OneTimeActionPublisherContract<SearchContract.SearchOneTimeAction> {

    override val mutableActionFlow: MutableSharedFlow<SearchContract.SearchOneTimeAction>
        get() = createDefaultMutableActionFlow()

    override val uiState: StateFlow<SearchContract.UiState> = combine(
        flow = bleSearchUseCase.scanDeviceListFlow,
        flow2 = getAllDeviceIdUseCase()
    ) { scanDevices, localDeviceIdList ->
        val devices = scanDevices.filter { !localDeviceIdList.contains(it.deviceId) }
        if (devices.isEmpty()) {
            SearchContract.UiState.Loading
        } else {
            SearchContract.UiState.Success(devices = devices)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SearchContract.UiState.Loading
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
            addDeviceUseCase(
                parameter = AddDeviceUseCase.Param(
                    deviceId = scannedDevice.deviceId,
                    name = scannedDevice.name
                )
            )
            publish(SearchContract.SearchOneTimeAction.AddDeviceSuccess(scannedDevice.deviceId))
        }
    }

    override fun onCleared() {
        super.onCleared()
        bleSearchUseCase.stopBleScan()
    }
}