package com.ledvance.ble.repo

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.ble.utils.BleScanResultAggregator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanFilter
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanMode
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScannerSettings
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/28 16:54
 * Describe : BleRepository
 */
class BleRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deviceRegistry: DeviceRegistry
) {
    private val TAG = "BleRepository"

    private val bleScanner by lazy {
        BleScanner(context)
    }

    private val aggregator by lazy {
        BleScanResultAggregator()
    }

    @RequiresPermission(
        allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT],
        conditional = true
    )
    fun scanDevices(
        filters: List<BleScanFilter> = emptyList(),
        settings: BleScannerSettings = BleScannerSettings(scanMode = BleScanMode.SCAN_MODE_BALANCED),
    ) = bleScanner.scan(filters, settings)
        .map {
            Timber.tag(TAG).d("scanDevices() >>>>>> ${it.device.name}")
            aggregator.aggregateDevices(it)
                .distinctBy { it.address }.also { devices ->
                    deviceRegistry.onScanResult(devices)
                }
        }

    @RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT, conditional = true)
    suspend fun connectDevice(
        macAddress: String,
        scope: CoroutineScope,
        options: BleGattConnectOptions = BleGattConnectOptions()
    ): ClientBleGatt = withContext(Dispatchers.IO) {
        var connectDevice: ClientBleGatt?
        Timber.tag(TAG).i("connectDevice() $macAddress begin >>>>>>")
        var connectBlock = async {
            ClientBleGatt.Companion.connect(context, macAddress, scope, options)
        }
        connectDevice = connectBlock.await()
        Timber.tag(TAG)
            .i("connectDevice() $macAddress end <<<<<< ${connectDevice.isConnected}")
        var retryCount = 3
        while (connectDevice?.isConnected != true && retryCount > 0) {
            retryCount--
            Timber.tag(TAG).i("connectDevice() retry connecting $macAddress begin >>>>>>")
            connectBlock = async {
                ClientBleGatt.connect(context, macAddress, scope, options)
            }
            connectDevice = connectBlock.await()
            Timber.tag(TAG)
                .i("connectDevice() retry connecting $macAddress end <<<<<< ${connectDevice.isConnected}")
            delay(100)
        }
        Timber.tag(TAG)
            .i("connectDevice() connect $macAddress isSuccessfully ${retryCount > 0}")
        return@withContext connectDevice
    }

    fun resetScanDevicesCache() {
        Timber.tag(TAG).d("resetScanDevicesCache")
        aggregator.reset()
    }
}