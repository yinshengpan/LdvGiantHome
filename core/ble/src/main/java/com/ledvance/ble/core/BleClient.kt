package com.ledvance.ble.core

import android.annotation.SuppressLint
import com.ledvance.ble.bean.ConnectionState
import com.ledvance.ble.constant.Constants
import com.ledvance.ble.protocol.BleProtocol
import com.ledvance.ble.protocol.GiantProtocol
import com.ledvance.ble.repo.BleRepository
import com.ledvance.domain.bean.DeviceId
import com.ledvance.utils.extensions.tryCatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
import no.nordicsemi.android.kotlin.ble.core.data.GattConnectionState
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
    private val onNotificationReceived: ((ByteArray) -> Unit)? = null,
    private val onConnectChange: ((DeviceId, ConnectionState) -> Unit)? = null
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

    suspend fun connect(): Boolean {
        Timber.tag(TAG).d("--> START connect(deviceId=$deviceId)")
        try {
            _state.value = ConnectionState.CONNECTING
            onConnectChange?.invoke(deviceId, ConnectionState.CONNECTING)
            val macAddress = deviceId.macAddress
            Timber.tag(TAG).d("connect: attempting to connect to $macAddress")
            val gatt = withTimeoutOrNull(15_000) {
                bleRepository.connectDevice(macAddress, scope)
            } ?: run {
                Timber.tag(TAG).e("connect: connection timeout")
                return fail("timeout")
            }

            Timber.tag(TAG).d("connect: connection established, discovering services...")
            val service = gatt.discoverServices()
                .findService(Constants.SERVICE_UUID_GIANT)
                ?: run {
                    Timber.tag(TAG).e("connect: main service not found (${Constants.SERVICE_UUID_GIANT})")
                    return fail("service not found")
                }

            writeChar = service.findCharacteristic(Constants.WRITE_CHAR_UUID_GIANT)
                ?: run {
                    Timber.tag(TAG).e("connect: write characteristic not found")
                    return fail("rx not found")
                }

            notifyChar = service.findCharacteristic(Constants.NOTIFY_CHAR_UUID_GIANT)
                ?: run {
                    Timber.tag(TAG).e("connect: notify characteristic not found")
                    return fail("tx not found")
                }

            Timber.tag(TAG).d("connect: requesting MTU 517...")
            tryCatch { gatt.requestMtu(517) }

            this.gatt = gatt
            observeNotify()
            observeConnectionState()

            _state.value = ConnectionState.CONNECTED
            onConnectChange?.invoke(deviceId, ConnectionState.CONNECTED)
            Timber.tag(TAG).d("connect: fully initialized, syncing device info/time")
            protocol.queryDeviceInfo()
            protocol.syncCurrentTime()
            Timber.tag(TAG).d("<-- END connect: success")
            return true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "connect: exception occurred")
            return fail("exception ${e.stackTraceToString()}")
        }
    }

    fun disconnect() {
        Timber.tag(TAG).d("disconnect(deviceId=$deviceId) - gatt is null: ${gatt == null}")
        try {
            gatt?.disconnect()
            gatt?.close()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error during gatt disconnect/close")
        } finally {
            gatt = null
            writeChar = null
            notifyChar = null
            _state.value = ConnectionState.DISCONNECTED
            onConnectChange?.invoke(deviceId, ConnectionState.DISCONNECTED)
        }
    }

    fun close() {
        Timber.tag(TAG).i("close(deviceId=$deviceId)")
        disconnect()
        scope.cancel()
    }

    private suspend fun observeNotify() {
        Timber.tag(TAG).d("observeNotify: starting notification stream")
        notifyChar?.getNotifications()
            ?.onEach {
                val bytes = it.value
                Timber.tag(TAG).d("onNotificationReceived: ${bytes.toHexString().uppercase()}")
                onNotificationReceived?.invoke(bytes)
            }
            ?.catch {
                Timber.tag(TAG).e(it, "observeNotify: stream error")
            }
            ?.launchIn(scope)
    }

    private fun observeConnectionState() {
        gatt?.connectionStateWithStatus?.onEach { status ->
            Timber.tag(TAG).i("observeConnectionState: device $deviceId status $status")
            val newState = when (status?.state) {
                GattConnectionState.STATE_DISCONNECTED -> ConnectionState.DISCONNECTED
                GattConnectionState.STATE_CONNECTING -> ConnectionState.CONNECTING
                GattConnectionState.STATE_CONNECTED -> ConnectionState.CONNECTED
                GattConnectionState.STATE_DISCONNECTING -> ConnectionState.DISCONNECTED
                null -> ConnectionState.DISCONNECTED
            }
            if (_state.value != newState) {
                _state.value = newState
                if (newState == ConnectionState.DISCONNECTED) {
                    writeChar = null
                    notifyChar = null
                }
                onConnectChange?.invoke(deviceId, newState)
            }
        }?.catch {
            Timber.tag(TAG).e(it, "observeConnectionState: error")
        }?.launchIn(scope)
    }

    @SuppressLint("MissingPermission")
    suspend fun write(data: ByteArray, writeType: BleWriteType = BleWriteType.NO_RESPONSE, isDelay: Boolean = true): Boolean {
        if (isDelay) {
            delay(Constants.FRAME_INTERVAL_MS)
        }
        val snapshot = writeChar
        if (snapshot == null) {
            Timber.tag(TAG).e("write failed: writeChar is null for device $deviceId")
            return false
        }
        val dataStr = data.toHexString().uppercase()
        Timber.tag(TAG).d("write: $dataStr (type=$writeType)")
        try {
            snapshot.write(DataByteArray(data), writeType)
            return true
        } catch (e: Throwable) {
            Timber.tag(TAG).e(e, "write failed: $dataStr")
            return false
        }
    }

    private fun fail(msg: String): Boolean {
        Timber.tag(TAG).e("fail: $msg")
        _state.value = ConnectionState.FAILED
        onConnectChange?.invoke(deviceId, ConnectionState.FAILED)
        return false
    }
}