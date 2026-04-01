package com.ledvance.ble.core

import com.ledvance.ble.bean.BleDeviceState
import com.ledvance.ble.bean.ConnectionState
import com.ledvance.ble.repo.BleRepository
import com.ledvance.domain.bean.DeviceId
import com.ledvance.utils.extensions.toHex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:22
 * Describe : ConnectionManager
 *
 * 蓝牙通知帧格式 (14 bytes for QueryDeviceInfo response):
 * [0x1D][CMD][0x02][Byte3..ByteN][0xD1]
 *
 * QueryDeviceInfo  响应 (14 bytes):
 *   [3]=开关(0x0F=On), [4]=R, [5]=G, [6]=B, [7]=W,
 *   [8]=亮度, [9]=场景/经典模式号, [10]=modeId, [11]=速度
 *
 * QueryDeviceState 响应 (9 bytes):
 *   [3]=开关(0x0F=On)
 *
 * GetTimingInfo 响应 (13 bytes):
 *   [3]=开灯开关, [4]=开灯时, [5]=开灯分, [6]=开灯周期
 *   [7]=关灯开关, [8]=关灯时, [9]=关灯分, [10]=关灯周期
 */
@Singleton
class ConnectionManager @Inject constructor(
    private val registry: DeviceRegistry,
    private val bleRepository: BleRepository,
) {

    private val TAG = "ConnectionManager"

    private val scope = CoroutineScope(Dispatchers.IO)

    private val connectionMap = mutableMapOf<DeviceId, BleClient>()

    /**
     * Android 平台 BLE GATT 连接上限通常为 7。
     * 超过此数会触发 LRU+RSSI 淘汰策略，释放最久未活跃 / 信号最弱的连接槽。
     */
    private val MAX_CONNECTION = 7

    fun requestConnect(deviceId: DeviceId) {
        Timber.tag(TAG).d("requestConnect: $deviceId")
        val existing = connectionMap[deviceId]
        if (existing != null) {
            val s = existing.state.value
            if (s == ConnectionState.CONNECTED || s == ConnectionState.CONNECTING) {
                Timber.tag(TAG).d("requestConnect: Device $deviceId already $s, skipping request")
                return
            }
            Timber.tag(TAG).i("requestConnect: Device $deviceId found in stale state $s, closing before reconnect")
            connectionMap.remove(deviceId)?.close()
        }

        if (connectionMap.size >= MAX_CONNECTION) {
            val evict = selectEvictDevice()
            Timber.tag(TAG).i("requestConnect: Reached MAX_CONNECTION ($MAX_CONNECTION), evicting: $evict")
            disconnect(evict)
        }

        connect(deviceId)
    }

    private fun connect(deviceId: DeviceId) {
        Timber.tag(TAG).d("connect: Initializing BleClient for $deviceId")
        registry.updateConnection(deviceId, ConnectionState.CONNECTING)

        val client = BleClient(
            deviceId = deviceId,
            bleRepository = bleRepository,
            onNotificationReceived = { bytes ->
                handleNotification(deviceId, bytes)
            },
            onConnectChange = { id, state ->
                Timber.tag(TAG).i("onConnectChange: Device $id changed state to $state")
                if (state == ConnectionState.DISCONNECTED || state == ConnectionState.FAILED) {
                    connectionMap.remove(id)?.close()
                }
                registry.updateConnection(id, state)
            }
        )
        connectionMap[deviceId] = client

        scope.launch {
            try {
                Timber.tag(TAG).d("connect: Launching connection coroutine for $deviceId")
                client.connect()
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "connect: Failed to connect to $deviceId")
                connectionMap.remove(deviceId)
                registry.updateConnection(deviceId, ConnectionState.FAILED)
            }
        }
    }

    /**
     * 解析设备蓝牙通知帧
     * 合法帧: 首字节 0x1D, 末字节 0xD1, Byte2 = 0x02 (Response)
     */
    private fun handleNotification(deviceId: DeviceId, bytes: ByteArray) {
        Timber.tag(TAG).v("handleNotification: Received from $deviceId: ${bytes.toHex()}")
        val parser = ProtocolFactory.createParser(deviceId, registry)
        parser.parse(deviceId, bytes)
    }

    fun disconnect(deviceId: DeviceId) {
        Timber.tag(TAG).i("disconnect: Manually disconnecting $deviceId")
        connectionMap.remove(deviceId)?.close()
        registry.updateConnection(deviceId, ConnectionState.DISCONNECTED)
    }

    fun disconnectAll() {
        Timber.tag(TAG).d("disconnectAll")
        connectionMap.forEach { (deviceId, client) ->
            client.disconnect()
            registry.updateConnection(deviceId, ConnectionState.DISCONNECTED)
        }
        connectionMap.clear()
    }

    fun getClient(deviceId: DeviceId): BleClient? = connectionMap[deviceId]

    /** 判断设备是否已连接或正在连接中（用于自动连接策略的上限检查） */
    fun isConnectedOrConnecting(deviceId: DeviceId): Boolean {
        val state = connectionMap[deviceId]?.state?.value
        return state == ConnectionState.CONNECTED || state == ConnectionState.CONNECTING
    }

    /** 当前已占用的连接槽数量 */
    fun connectedCount(): Int = connectionMap.size

    private fun selectEvictDevice(): DeviceId {
        val now = System.currentTimeMillis()
        val idleThreshold = 60_000L * 5 // 5 minutes

        val victim = connectionMap.keys
            .mapNotNull { registry.get(it) }
            .sortedWith(
                compareBy<BleDeviceState> { state ->
                    // Group devices idle for > 5 mins together as '0L' so they are evicted first based on RSSI
                    if (now - state.lastActiveTime > idleThreshold) 0L else state.lastActiveTime
                }.thenBy { it.rssi } // Lowest RSSI evicted first among ties/idle devices
            )
            .first().deviceId
        
        Timber.tag(TAG).d("selectEvictDevice: Selected $victim for eviction based on LRU/RSSI strategy")
        return victim
    }
}