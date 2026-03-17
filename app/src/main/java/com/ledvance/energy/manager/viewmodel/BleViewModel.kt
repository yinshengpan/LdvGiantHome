package com.ledvance.energy.manager.viewmodel

import android.Manifest
import android.os.SystemClock
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.DLBBleUseCase
import com.ledvance.ble.bean.ConnectStatus
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.database.repo.DeviceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    private val bleUseCase: DLBBleUseCase,
    private val deviceRepo: DeviceRepo,
) : ViewModel() {
    private val TAG = "BleViewModel"
    private val _deviceOnline = MutableStateFlow<Boolean>(false)
    private val _scanResult = MutableStateFlow<List<ScannedDevice>>(emptyList())
    private val _addChargerRestarting = MutableStateFlow<String?>(null)
    val addChargerRestarting: StateFlow<String?> = _addChargerRestarting
    val scanDevices = _scanResult.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf()
    )
    private var scanJob: Job? = null
    private var connectingJob: Job? = null
    private var currentPolling: Job? = null
    private var currentAddCharger: Job? = null
    private val bleMutex = Mutex()

    @Volatile
    private var canPolling: Boolean = true

    fun setCanPolling(can: Boolean) {
        canPolling = can
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startBleScan() {
        stopBleScan()
        Timber.tag(TAG).i("startBleScan")
        viewModelScope.launch(Dispatchers.IO) {
            scanJob = bleUseCase.startScan().onEach { devices ->
                Timber.tag(TAG)
                    .d("startBleScan scanResult-> ${devices.joinToString(",") { it.name }}")
                _scanResult.update {
                    devices.filter { !it.hasOutOfRange(SystemClock.elapsedRealtime()) }
                        .map { it.copy(scanTime = 0) }
                }
            }.catch {
                Timber.tag(TAG).e(it, "startBleScan")
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
        }
    }

    fun stopBleScan() {
        scanJob?.also {
            Timber.tag(TAG).i("stopBleScan")
            scanJob?.cancel()
            scanJob = null
        }
    }

    fun getConnectStatusFlow(): StateFlow<ConnectStatus> {
        return bleUseCase.getConnectStatusFlow()
    }

    fun getDeviceOnlineFlow(): StateFlow<Boolean> {
        return _deviceOnline
    }

    fun cancelConnecting() {
        connectingJob?.also {
            Timber.tag(TAG).i("cancelConnecting")
            connectingJob?.cancel()
            connectingJob = null
            _deviceOnline.update { false }
        }
    }

    fun disconnect() {
        cancelConnecting()
        Timber.tag(TAG).i("disconnect")
        bleUseCase.disconnect()
        _deviceOnline.update { false }
    }

    fun connectDevice(device: ScannedDevice) {
        connect(device) {
        }
    }

    fun connect(device: ScannedDevice, after: suspend (Boolean) -> Unit = {}) {
        stopBleScan()
        cancelConnecting()
        Timber.tag(TAG).i("connect ${device.name}(${device.address})")
        connectingJob = viewModelScope.launch {
            val isConnected = bleUseCase.connect(
                scannedDevice = device, viewModelScope = viewModelScope
            )
            Timber.tag(TAG).i("connect ${device.name}(${device.address}) isConnected:$isConnected")
            after.invoke(isConnected)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.tag(TAG).i("viewModel onCleared")
        disconnect()
    }
}