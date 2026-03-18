package com.ledvance.ble.usecase

import android.Manifest
import androidx.annotation.RequiresPermission
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.ble.constant.Constants
import com.ledvance.ble.repo.BleRepository
import com.ledvance.utils.BluetoothManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 13:08
 * Describe : BleSearchUseCase
 */
@Singleton
class BleSearchUseCase @Inject constructor(val bleRepository: BleRepository) {
    private val TAG = "BleUseCase"
    private val coroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }
    private var scanJob: Job? = null

    private val _scanDeviceListFlow = MutableStateFlow<List<ScannedDevice>>(listOf())

    val scanDeviceListFlow: StateFlow<List<ScannedDevice>> = _scanDeviceListFlow

    @OptIn(FlowPreview::class)
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        if (scanJob != null) {
            Timber.tag(TAG).w("Scan already running")
            return
        }
        Timber.tag(TAG).i("startScan()")
        scanJob = coroutineScope.launch {
            BluetoothManager.checkBleScanDeviceFrequently()
            bleRepository.scanDevices()
                .cancellable()
                .sample(500L)
                .distinctUntilChanged()
                .collectLatest { devices ->
                    _scanDeviceListFlow.update { devices }
                }
        }

    }

    fun stopBleScan() {
        scanJob?.also {
            Timber.tag(TAG).i("stopBleScan")
            scanJob?.cancel()
        }
        scanJob = null
    }

    fun clear() {
        _scanDeviceListFlow.update { emptyList() }
    }
}