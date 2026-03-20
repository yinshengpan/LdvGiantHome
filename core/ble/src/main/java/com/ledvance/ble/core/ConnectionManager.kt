package com.ledvance.ble.core

import com.ledvance.ble.bean.BleDeviceState
import com.ledvance.ble.bean.ConnectionState
import com.ledvance.ble.protocol.GiantProtocol
import com.ledvance.ble.repo.BleRepository
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceTimer
import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.command.CommandType
import com.ledvance.domain.bean.command.NotifyType
import com.ledvance.domain.bean.command.OnOff
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val scope = CoroutineScope(Dispatchers.IO)

    private val connectionMap = mutableMapOf<DeviceId, BleClient>()

    private val MAX_CONNECTION = 3

    fun requestConnect(deviceId: DeviceId) {
        if (connectionMap.containsKey(deviceId)) return

        if (connectionMap.size >= MAX_CONNECTION) {
            val evict = selectEvictDevice()
            disconnect(evict)
        }

        connect(deviceId)
    }

    private fun connect(deviceId: DeviceId) {
        registry.updateConnection(deviceId, ConnectionState.CONNECTING)

        val client = BleClient(deviceId, bleRepository) { bytes ->
            handleNotification(deviceId, bytes)
        }
        connectionMap[deviceId] = client

        scope.launch {
            try {
                client.connect()
                registry.updateConnection(deviceId, ConnectionState.CONNECTED)
            } catch (e: Exception) {
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
        if (bytes.size < 4) return
        if (bytes.first() != GiantProtocol.HEADER_BYTE) return
        if (bytes.last() != GiantProtocol.END_BYTE) return
        if (bytes[2] != NotifyType.Response.command) return

        val cmd = bytes[1]
        when (cmd) {
            CommandType.QueryDeviceInfo.command -> parseQueryDeviceInfo(deviceId, bytes)
            CommandType.QueryDeviceState.command -> parseQueryDeviceState(deviceId, bytes)
            CommandType.GetTimingInfo.command -> parseGetTimingInfo(deviceId, bytes)
            CommandType.QueryCurrentTime.command -> parseQueryCurrentTime(deviceId, bytes)
        }
    }

    /**
     * QueryDeviceInfo 响应 — 14 bytes
     * [0x1D][0x00][0x02][开关][R][G][B][W][亮度][经典/场景模式号][modeId][速度][0xFF][0xD1]
     */
    private fun parseQueryDeviceInfo(deviceId: DeviceId, bytes: ByteArray) {
        if (bytes.size != 14) return
        val power      = bytes[3] == OnOff.On.command
        val r          = bytes[4].toInt() and 0xFF
        val g          = bytes[5].toInt() and 0xFF
        val b          = bytes[6].toInt() and 0xFF
        val w          = bytes[7].toInt() and 0xFF
        val brightness = bytes[8].toInt() and 0xFF
        val modeType   = bytes[9].toInt() and 0xFF
        val modeId     = bytes[10].toInt() and 0xFF
        val speed      = bytes[11].toInt() and 0xFF
        registry.updateDeviceInfo(deviceId, power, r, g, b, w, brightness, modeType, modeId, speed)
    }

    /**
     * QueryDeviceState 响应 — 9 bytes
     * [0x1D][0x15][0x02][开关][0xFF][0xFF][0xFF][0xFF][0xD1]
     */
    private fun parseQueryDeviceState(deviceId: DeviceId, bytes: ByteArray) {
        if (bytes.size != 9) return
        val power = bytes[3] == OnOff.On.command
        registry.updateDeviceState(deviceId, power)
    }

    /**
     * GetTimingInfo 响应 — 13 bytes
     * [0x1D][0x16][0x02][开灯开关][开灯时][开灯分][开灯周期][关灯开关][关灯时][关灯分][关灯周期][0xFF][0xD1]
     */
    private fun parseGetTimingInfo(deviceId: DeviceId, bytes: ByteArray) {
        if (bytes.size != 13) return
        val onSwitch  = bytes[3] == 0x01.toByte()
        val onHour    = bytes[4].toInt() and 0xFF
        val onMinute  = bytes[5].toInt() and 0xFF
        val onCycle   = bytes[6].toInt() and 0xFF
        val onTimer = DeviceTimer(
            deviceId = deviceId,
            timerType = TimerType.ON,
            enabled = onSwitch,
            hour = onHour,
            minute = onMinute,
            weekCycle = onCycle,
        )
        val offSwitch = bytes[7] == 0x01.toByte()
        val offHour   = bytes[8].toInt() and 0xFF
        val offMinute = bytes[9].toInt() and 0xFF
        val offCycle  = bytes[10].toInt() and 0xFF
        val offTimer = DeviceTimer(
            deviceId = deviceId,
            timerType = TimerType.OFF,
            enabled = offSwitch,
            hour = offHour,
            minute = offMinute,
            weekCycle = offCycle,
        )
        registry.updateTimerInfo(deviceId, onTimer, offTimer)
    }

    /**
     * QueryCurrentTime / SetCurrentTime 应答 — 9 bytes
     * [0x1D][0x0B][0x02][时][分][秒][星期][0xFF][0xD1]
     */
    private fun parseQueryCurrentTime(deviceId: DeviceId, bytes: ByteArray) {
        if (bytes.size != 9) return
        // 更新活跃时间（当前未持久化时间，可按需扩展 DeviceRegistry）
        registry.updateActive(deviceId)
    }

    fun disconnect(deviceId: DeviceId) {
        connectionMap.remove(deviceId)?.disconnect()
        registry.updateConnection(deviceId, ConnectionState.DISCONNECTED)
    }

    fun getClient(deviceId: DeviceId): BleClient? = connectionMap[deviceId]

    private fun selectEvictDevice(): DeviceId {
        val now = System.currentTimeMillis()
        val idleThreshold = 60_000L * 5 // 5 minutes

        return connectionMap.keys
            .mapNotNull { registry.get(it) }
            .sortedWith(
                compareBy<BleDeviceState> { state ->
                    // Group devices idle for > 5 mins together as '0L' so they are evicted first based on RSSI
                    if (now - state.lastActiveTime > idleThreshold) 0L else state.lastActiveTime
                }.thenBy { it.rssi } // Lowest RSSI evicted first among ties/idle devices
            )
            .first().deviceId
    }
}