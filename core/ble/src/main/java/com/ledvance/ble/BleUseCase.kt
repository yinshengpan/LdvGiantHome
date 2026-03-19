package com.ledvance.ble

import android.Manifest
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import com.ledvance.ble.bean.ConnectStatus
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.ble.constant.Constants
import com.ledvance.ble.repo.BleRepository
import com.ledvance.utils.BluetoothManager
import com.ledvance.utils.extensions.tryCatch
import com.ledvance.utils.extensions.tryCatchReturn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanFilter
import no.nordicsemi.android.kotlin.ble.core.scanner.FilteredServiceUuid
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleUseCase @Inject constructor(
    val bleRepository: BleRepository,
) {
    private val TAG = "BleUseCase"
    private var scanDeviceList = listOf<ScannedDevice>()
    private var dlbBleClient: DLBBleClient? = null
    private val filterList = listOf(
        BleScanFilter(serviceUuid = FilteredServiceUuid(uuid = ParcelUuid(Constants.FILTER_SERVICE_UUID)))
    )
    private val connectStatusFlow = MutableStateFlow(ConnectStatus.Connecting)
    fun getConnectStatusFlow(): StateFlow<ConnectStatus> {
        return connectStatusFlow
    }

    @OptIn(FlowPreview::class)
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    suspend fun startScan(): Flow<List<ScannedDevice>> {
        BluetoothManager.checkBleScanDeviceFrequently()
        Timber.tag(TAG).i("startScan()")
        return bleRepository.scanDevices()
            .cancellable()
            .sample(500L)
            .map {
                scanDeviceList = it.distinctBy { it.address }
                scanDeviceList
            }
    }

    suspend fun connect(scannedDevice: ScannedDevice, viewModelScope: CoroutineScope): Boolean {
        try {
            connectStatusFlow.update { ConnectStatus.Connecting }
            val device = dlbBleClient?.device
            Timber.tag(TAG).i("connect() ${scannedDevice.name}")
            if (device?.address == scannedDevice.address && dlbBleClient?.isConnected() == true) {
                Timber.tag(TAG).i("connect() is already ${scannedDevice.name}")
                connectStatusFlow.update { ConnectStatus.Connected }
                return true
            }
            disconnect()
            val serverDevice = scanDeviceList.find {
                it.address == scannedDevice.address
            } ?: return let {
                Timber.tag(TAG).e("connect() device is not found")
                connectStatusFlow.update { ConnectStatus.Failed }
                false
            }
            val gatt = withTimeoutOrNull(15 * 1000) {
                bleRepository.connectDevice(serverDevice.address, viewModelScope)
            } ?: return let {
                Timber.tag(TAG).e("connect() timeout")
                connectStatusFlow.update { ConnectStatus.Failed }
                false
            }

            val services = gatt.discoverServices()
            val service = services.findService(Constants.SERVICE_UUID) ?: let {
                Timber.tag(TAG).e("connect() not found service")
                connectStatusFlow.update { ConnectStatus.Failed }
                return false
            }
            val rxChar = service.findCharacteristic(Constants.WRITE_CHAR_UUID) ?: let {
                Timber.tag(TAG).e("connect() not found rxChar")
                connectStatusFlow.update { ConnectStatus.Failed }
                return false
            }
            val txChar = service.findCharacteristic(Constants.NOTIFY_CHAR_UUID) ?: let {
                Timber.tag(TAG).e("nconnect() ot found txChar")
                connectStatusFlow.update { ConnectStatus.Failed }
                return false
            }
            tryCatch { gatt.requestMtu(517) }
            dlbBleClient = DLBBleClient(
                device = scannedDevice,
                gatt = gatt,
                rxChar = rxChar,
                txChar = txChar,
                scope = viewModelScope,
            )
            val connected = dlbBleClient?.isConnected()
            Timber.tag(TAG).i("connect() successfully $connected")
            connectStatusFlow.update { if (connected == true) ConnectStatus.Connected else ConnectStatus.Failed }
            return connected == true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "connect() failed!")
            connectStatusFlow.update { ConnectStatus.Failed }
            return false
        }
    }

    fun isConnected(): Boolean {
        return dlbBleClient?.isConnected() ?: false
    }

    suspend fun sendFile(file: File, onFileProgress: (Int, Int) -> Unit = { _, _ -> }): Boolean {
        return tryCatchReturn {
            val isSuccessfully = dlbBleClient?.sendOtaFile(file.readBytes(), onFileProgress)
            if (isSuccessfully == true) {
                bleRepository.resetScanDevicesCache()
                tryCatchReturn { dlbBleClient?.disconnect() }
                dlbBleClient = null
            }
            isSuccessfully
        } ?: false
    }

    fun getDevice(): ScannedDevice? = dlbBleClient?.device

    fun disconnect() {
        dlbBleClient?.also {
            Timber.tag(TAG).i("disconnect()")
            tryCatch { dlbBleClient?.disconnect() }
            connectStatusFlow.update { ConnectStatus.Connecting }
            dlbBleClient = null
        }
    }
}



