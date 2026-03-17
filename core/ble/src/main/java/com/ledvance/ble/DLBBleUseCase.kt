package com.ledvance.ble

import android.Manifest
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import com.ledvance.ble.bean.Configuration
import com.ledvance.ble.bean.ConnectStatus
import com.ledvance.ble.bean.DeviceConfiguration
import com.ledvance.ble.bean.Handshake
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.ble.constant.Constants
import com.ledvance.ble.repo.BleRepository
import com.ledvance.utils.BluetoothManager
import com.ledvance.utils.DeviceManager
import com.ledvance.utils.extensions.toHex
import com.ledvance.utils.extensions.toInt
import com.ledvance.utils.extensions.toUnsignedInt
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
class DLBBleUseCase @Inject constructor(
    val bleRepository: BleRepository,
) {
    private val TAG = "DLBBleUseCase"
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
        return bleRepository.scanDevices(filterList)
            .cancellable()
            .sample(500L)
            .map {
                scanDeviceList = it.distinctBy { it.address }
                scanDeviceList
            }
    }

    suspend fun connect(
        scannedDevice: ScannedDevice,
        viewModelScope: CoroutineScope
    ): Boolean {
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
            val rxChar = service.findCharacteristic(Constants.RX_CHAR_UUID) ?: let {
                Timber.tag(TAG).e("connect() not found rxChar")
                connectStatusFlow.update { ConnectStatus.Failed }
                return false
            }
            val txChar = service.findCharacteristic(Constants.TX_CHAR_UUID) ?: let {
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun sendHandshake(): Handshake? {
        val now = (System.currentTimeMillis() / 1000).toInt()
        val device = dlbBleClient?.device ?: return null
        Timber.tag(TAG).i("sendHandshake() sn->${device.sn}")
        try {
            val result = dlbBleClient?.sendHandshake(
                user = 0,
                userId = 1000008,
                permStartTs = now,
                permEndTs = Int.MAX_VALUE,
                clockTs = now,
                tzIndex = 12,
                tzStr = "00:00"
            )
            val data = result?.payload ?: return null
            var index = 0
            // DLB盒子 SN (16字节 ASCII)
            val snBytes = data.copyOfRange(index, index + 16)
            val sn = snBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
            index += 16
            Timber.tag(TAG).i("sendHandshake() result sn->$sn ${snBytes.toHex()}")
            if (sn != device.sn) {
                Timber.tag(TAG)
                    .e("sendHandshake() result SN verification failed deviceSN:${device.sn}")
                connectStatusFlow.update { ConnectStatus.Failed }
                return null
            }

            val userByte = data[index++]
            val user = userByte.toUnsignedInt()
            Timber.tag(TAG).i("sendHandshake() result user->$user ${userByte.toHex()}")

            val userIdBytes = data.copyOfRange(index, index + 4)
            val userId = userIdBytes.toInt()
            index += 4
            Timber.tag(TAG).i("sendHandshake() result userId->$userId ${userIdBytes.toHex()}")

            val statusByte = data[index++]
            val status = statusByte.toUnsignedInt()
            Timber.tag(TAG).i("sendHandshake() result status->$status ${statusByte.toHex()}")

            if (status == 0) {
                Timber.tag(TAG).e("sendHandshake() status->$status")
                connectStatusFlow.update { ConnectStatus.Failed }
                return null
            }

            val scheduleStartHByte = data[index++]
            val scheduleStartH = scheduleStartHByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("sendHandshake() result scheduleStartH->${scheduleStartH} ${scheduleStartHByte.toHex()}")

            val scheduleStartMByte = data[index++]
            val scheduleStartM = scheduleStartMByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("sendHandshake() result scheduleStartM->${scheduleStartM} ${scheduleStartMByte.toHex()}")

            val scheduleChargeCurrentByte = data[index++]
            val scheduleChargeCurrent = scheduleChargeCurrentByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("sendHandshake() result scheduleChargeCurrent->${scheduleChargeCurrent} ${scheduleChargeCurrentByte.toHex()}")

            val minChargeCurrentByte = data[index++]
            val minChargeCurrent = minChargeCurrentByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("sendHandshake() result minChargeCurrent->${minChargeCurrent} ${minChargeCurrentByte.toHex()}")

            val maxChargeCurrentByte = data[index++]
            val maxChargeCurrent = maxChargeCurrentByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("sendHandshake() result maxChargeCurrent->${maxChargeCurrent} ${maxChargeCurrentByte.toHex()}")

            val oclockBytes = data.copyOfRange(index, index + 4)
            val oclock = oclockBytes.toInt()
            index += 4
            Timber.tag(TAG).i("sendHandshake() result oclock->${oclock} ${oclockBytes.toHex()}")

            val connectedCountByte = data[index++]
            val connectedCount = connectedCountByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("sendHandshake() result connectedCount->${connectedCount} ${connectedCountByte.toHex()}")

            val fwVersionLeftByte = data[index++]
            val fwVersionLeft = fwVersionLeftByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("sendHandshake() result fwVersionLeft->${fwVersionLeft} ${fwVersionLeftByte.toHex()}")

            val fwVersionRightByte = data[index++]
            val fwVersionRight = fwVersionRightByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("sendHandshake() result fwVersionRight->${fwVersionRight} ${fwVersionRightByte.toHex()}")

            val bleVersionByte = data[index++]
            val bleVersion = (bleVersionByte.toUnsignedInt()).toString()
            Timber.tag(TAG)
                .i("sendHandshake() result bleVersion->${bleVersion} ${bleVersionByte.toHex()}")

            val chargeCountBytes = data.copyOfRange(index, index + 4)
            val chargeCount = chargeCountBytes.toInt()
            index += 4
            Timber.tag(TAG)
                .i("sendHandshake() result chargeCount->${chargeCount} ${chargeCountBytes.toHex()}")

            val faultCountBytes = data.copyOfRange(index, index + 4)
            val faultCount = faultCountBytes.toInt()
            index += 4
            Timber.tag(TAG)
                .i("sendHandshake() result faultCount->${faultCount} ${faultCountBytes.toHex()}")

            // 0x01 开启，0x02 关闭
            val plugAndChargeEnabledByte = data[index++]
            val plugAndChargeEnabled = (plugAndChargeEnabledByte.toUnsignedInt()) == 0x01
            Timber.tag(TAG)
                .i("sendHandshake() result plugAndChargeEnabled->${plugAndChargeEnabled} ${plugAndChargeEnabledByte.toHex()}")

            val networkModeByte = data[index++]
            val networkMode = networkModeByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("sendHandshake() result networkMode->${networkMode} ${networkModeByte.toHex()}")

            val boxTypeByte = data[index++]
            val boxType = boxTypeByte.toUnsignedInt()
            Timber.tag(TAG).i("sendHandshake() result boxType->${boxType} ${boxTypeByte.toHex()}")

            val handshake = Handshake(
                address = device.address,
                name = device.name,
                sn = device.sn,
                user = user,
                userId = userId,
                scheduleStartTime = "$scheduleStartH:$scheduleStartM",
                scheduleChargeCurrent = scheduleChargeCurrent,
                maxChargeCurrent = maxChargeCurrent,
                minChargeCurrent = minChargeCurrent,
                firmwareVersion = "V${fwVersionLeft}.${fwVersionRight}",
                plugAndChargeEnabled = plugAndChargeEnabled,
                networkMode = networkMode,
                bleVersion = bleVersion,
                chargeCount = chargeCount,
                faultCount = faultCount,
                boxType = boxType
            )
            Timber.tag(TAG).i("sendHandshake() handshake->$handshake")
            connectStatusFlow.update { ConnectStatus.Completed }
            return handshake
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "sendHandshake()")
            connectStatusFlow.update { ConnectStatus.Failed }
            return null
        }
    }

    suspend fun queryChargerPile(isPaired: Boolean): List<String>? {
        return tryCatchReturn {
            val result = dlbBleClient?.getChargerPile(
                user = if (isPaired) 0 else 1,
                userId = 1000008,
            )
            val data = result?.payload ?: return emptyList()
            var index = 0
            // DLB盒子 SN (16字节 ASCII)
            val snBytes = data.copyOfRange(index, index + 16)
            val sn = snBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
            index += 16
            Timber.tag(TAG)
                .i("queryChargerPile(isPaired:$isPaired) result sn->${sn} ${snBytes.toHex()}")

            // 桩数量
            val pileCountByte = data[index++]
            val pileCount = pileCountByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("queryChargerPile(isPaired:$isPaired) result pileCount->${pileCount} ${pileCountByte.toHex()}")

            // 桩号 (16*n)
            val pileNumbers = mutableListOf<String>()
            for (i in 0 until pileCount) {
                val pileBytes = data.copyOfRange(index, index + 16)
                val pileNumber = pileBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
                Timber.tag(TAG)
                    .i("queryChargerPile(isPaired:$isPaired) result pileNumber->${pileNumber} ${pileBytes.toHex()}")
                pileNumbers.add(pileNumber)
                index += 16
            }
            Timber.tag(TAG)
                .i("queryChargerPile(isPaired:$isPaired),sn:$sn,pileCount:$pileCount,pileNumbers:$pileNumbers")
            pileNumbers
        }
    }

    suspend fun editChargingPile(chargeNumber: String, isDelete: Boolean): Boolean {
        return tryCatchReturn {
            val result = dlbBleClient?.editChargerPile(chargeNumber, isDelete)
            val data = result?.payload ?: return false
            var index = 0
            // DLB盒子 SN (16字节 ASCII)
            val snBytes = data.copyOfRange(index, index + 16)
            val sn = snBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
            index += 16
            Timber.tag(TAG)
                .i("editChargingPile(chargeNumber:$chargeNumber,isDelete:$isDelete) result sn->${sn} ${snBytes.toHex()}")

            val statusByte = data[index++]
            val status = statusByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("editChargingPile(chargeNumber:$chargeNumber,isDelete:$isDelete) result status->${status} ${statusByte.toHex()}")

            val isSuccessfully = status == 0
            Timber.tag(TAG)
                .i("editChargingPile(chargeNumber:$chargeNumber,isDelete:$isDelete),sn:$sn,isSuccessfully:$isSuccessfully")
            isSuccessfully
        } ?: false
    }

    suspend fun setConfiguration(configuration: DeviceConfiguration, value: String): Boolean {
        return tryCatchReturn {
            val result = dlbBleClient?.setConfiguration(configuration, value)
            val data = result?.payload ?: return false
            var index = 0
            // DLB盒子 SN (16字节 ASCII)
            val snBytes = data.copyOfRange(index, index + 16)
            val sn = snBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
            index += 16
            Timber.tag(TAG)
                .i("setConfiguration(configuration:$configuration,value:$value) result sn->${sn} ${snBytes.toHex()}")

            val statusByte = data[index++]
            val status = statusByte.toUnsignedInt()
            Timber.tag(TAG)
                .i("setConfiguration(configuration:$configuration,value:$value) result status->${status} ${statusByte.toHex()}")

            val isSuccessfully = status == 0
            Timber.tag(TAG)
                .i("setConfiguration(configuration:$configuration,value:$value),sn:$sn,isSuccessfully:$isSuccessfully")
            isSuccessfully
        } ?: false
    }

    suspend fun getConfiguration(configuration: DeviceConfiguration): Configuration? {
        return tryCatchReturn {
            val result = dlbBleClient?.getConfiguration(configuration)
            val data = result?.payload ?: return null
            var index = 0
            // DLB盒子 SN (16字节 ASCII)
            val snBytes = data.copyOfRange(index, index + 16)
            val sn = snBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
            index += 16
            Timber.tag(TAG)
                .i("getConfiguration(configuration:$configuration) result sn->${sn} ${snBytes.toHex()}")

            val nameLengthBytes = data.copyOfRange(index, index + 2)
            val nameLength = nameLengthBytes.toInt()
            index += 2
            Timber.tag(TAG)
                .i("getConfiguration(configuration:$configuration) result nameLength->${nameLength} ${nameLengthBytes.toHex()}")

            val valueLengthBytes = data.copyOfRange(index, index + 2)
            val valueLength = valueLengthBytes.toInt()
            index += 2
            Timber.tag(TAG)
                .i("getConfiguration(configuration:$configuration) result valueLength->${valueLength} ${valueLengthBytes.toHex()}")

            val nameBytes = data.copyOfRange(index, index + nameLength)
            val name = nameBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
            index += nameLength
            Timber.tag(TAG)
                .i("getConfiguration(configuration:$configuration) result name->${name} ${nameBytes.toHex()}")

            val valueBytes = data.copyOfRange(index, index + valueLength)
            val value = valueBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
            index++
            Timber.tag(TAG)
                .i("getConfiguration(configuration:$configuration) result value->${value} ${valueBytes.toHex()}")

            Timber.tag(TAG)
                .i("getConfiguration(configuration:$configuration),sn:$sn,name:$name,value:$value")
            Configuration(configuration, value)
        }
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



