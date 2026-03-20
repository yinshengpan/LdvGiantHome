package com.ledvance.ble.core

import android.annotation.SuppressLint
import com.ledvance.ble.bean.ConnectionState
import com.ledvance.ble.constant.Constants
import com.ledvance.ble.protocol.BleProtocol
import com.ledvance.ble.protocol.GiantProtocol
import com.ledvance.ble.repo.BleRepository
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.asMacAddress
import com.ledvance.utils.extensions.tryCatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeoutOrNull
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.data.BleWriteType
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:27
 * Describe : BleClient
 */
class BleClient(
    private val deviceId: DeviceId,
    private val bleRepository: BleRepository,
    private val onNotificationReceived: ((ByteArray) -> Unit)? = null
) {

    private val TAG = "BleClient"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var gatt: ClientBleGatt? = null
    private var writeChar: ClientBleGattCharacteristic? = null
    private var notifyChar: ClientBleGattCharacteristic? = null
    val commandQueue = CommandQueue()
    val protocol: BleProtocol by lazy { GiantProtocol(this, commandQueue) }

    private val _state = MutableStateFlow(ConnectionState.DISCONNECTED)
    val state: StateFlow<ConnectionState> = _state

    val isConnected: Boolean
        get() = _state.value == ConnectionState.CONNECTED

    suspend fun connect() : Boolean{
        try {
            _state.value = ConnectionState.CONNECTING
            val gatt = withTimeoutOrNull(15_000) {
                bleRepository.connectDevice(deviceId.asMacAddress(), scope)
            } ?: return fail("timeout")

            val service = gatt.discoverServices()
                .findService(Constants.SERVICE_UUID)
                ?: return fail("service not found")

            writeChar = service.findCharacteristic(Constants.WRITE_CHAR_UUID)
                ?: return fail("rx not found")

            notifyChar = service.findCharacteristic(Constants.NOTIFY_CHAR_UUID)
                ?: return fail("tx not found")

            tryCatch { gatt.requestMtu(517) }

            this.gatt = gatt

            observeNotify()

            _state.value = ConnectionState.CONNECTED
            protocol.queryDeviceInfo()
            protocol.syncCurrentTime()
            return true
        } catch (e: Exception) {
            return fail("exception ${e.stackTraceToString()}")
        }
    }

    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        writeChar = null
        notifyChar = null
        _state.value = ConnectionState.DISCONNECTED
    }

    private suspend fun observeNotify() {
        notifyChar?.getNotifications()
            ?.onEach {
                val bytes = it.value
                Timber.d("observeNotify ${it.value.toHexString().uppercase()}")
                onNotificationReceived?.invoke(bytes)
            }
            ?.catch {
                Timber.tag(TAG).e(it, "getNotifications")
            }
            ?.launchIn(scope)
    }

    @SuppressLint("MissingPermission")
    suspend fun write(data: ByteArray, writeType: BleWriteType = BleWriteType.NO_RESPONSE) {
        delay(Constants.FRAME_INTERVAL_MS)
        Timber.tag(TAG).d("write ${data.toHexString()}")
        tryCatch { writeChar?.write(DataByteArray(data), writeType) }
    }

    private fun fail(msg: String): Boolean {
        Timber.tag(TAG).e(msg)
        _state.value = ConnectionState.FAILED
        return false
    }
}