package com.ledvance.usecase.device

import com.ledvance.ble.core.ConnectionManager
import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.ble.repo.BleRepository
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.UseCase
import com.ledvance.utils.BluetoothManager
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : AutoConnectUseCase — 分段扫描驱动的自动连接策略
 *
 * 扫描节奏:
 *   - 活跃扫描窗口:  SCAN_DURATION_MS       (10s) 每轮主动扫描时长
 *   - 扫描间隔暂停:  BluetoothManager.ensureBleScanInterval() 两轮扫描之间的冷却期，防止持续耗电
 *   - 全部已连时休眠: ALL_CONNECTED_IDLE_MS (30s) 所有注册设备均已连接时无需扫描
 *
 * 连接优先级: 按 RSSI 降序，信号最强的设备最先被 requestConnect，成功率更高。
 *
 * 与 ConnectionManager 协作:
 *   - requestConnect 内部已处理「已连接/连接中」去重，调用幂等安全
 *   - isConnectedOrConnecting() 用于判断是否还有设备需要连接
 *   - connectedCount() 用于日志追踪
 */
@ViewModelScoped
class AutoConnectUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val dispatcher: CoroutineDispatcher,
    private val bleRepository: BleRepository,
    private val connectionManager: ConnectionManager,
    private val deviceRepo: DeviceRepo,
    private val deviceRegistry: DeviceRegistry,
) : UseCase<CoroutineScope, Job>() {

    private val TAG = "AutoConnectUseCase"

    companion object {
        /** 每次主动扫描的持续时长 */
        private const val SCAN_DURATION_MS = 10_000L

        /** 所有注册设备均已连接时的休眠时长（避免无意义扫描） */
        private const val ALL_CONNECTED_IDLE_MS = 30_000L
    }

    override fun execute(parameter: CoroutineScope): Job {
        return parameter.launch(dispatcher) {
            while (true) {
                // ── 1. 获取当前已注册的设备 ID 集合 ─────────────────────────────
                val registeredIds = deviceRepo.getDeviceListFlow()
                    .map { list -> list.map { it.device.deviceId }.toSet() }
                    .first()

                if (registeredIds.isEmpty()) {
                    Timber.tag(TAG).d("No registered devices, idle...")
                    BluetoothManager.ensureBleScanInterval()
                    continue
                }

                // ── 2. 若所有注册设备都已连接/连接中，进入长休眠 ───────────────
                val allConnectedOrConnecting = registeredIds.all { id ->
                    connectionManager.isConnectedOrConnecting(id)
                }

                if (allConnectedOrConnecting) {
                    Timber.tag(TAG).d(
                        "All ${registeredIds.size} registered devices connected/connecting, " +
                                "idle ${ALL_CONNECTED_IDLE_MS}ms"
                    )
                    delay(ALL_CONNECTED_IDLE_MS)
                    continue
                }

                val pendingCount = registeredIds.count { !connectionManager.isConnectedOrConnecting(it) }
                Timber.tag(TAG).d(
                    "Scan burst start — registered=${registeredIds.size}, " +
                            "pending=$pendingCount, slots=${connectionManager.connectedCount()}"
                )

                // ── 3. 执行一次时限扫描窗口（自动在超时后停止）─────────────────
                withTimeoutOrNull(SCAN_DURATION_MS) {
                    bleRepository.scanDevices().collect { scannedDevices ->
                        val nearbyRegistered = scannedDevices
                            .filter { it.deviceId in registeredIds }
                            .sortedByDescending { it.rssi } // 信号最强优先

                        if (nearbyRegistered.isEmpty()) return@collect

                        val nearbyDeviceInfo = nearbyRegistered.joinToString {
                            "${it.deviceId.macAddress}(rssi=${it.rssi})"
                        }
                        Timber.tag(TAG).d("Scan hit ${nearbyRegistered.size} registered device(s): $nearbyDeviceInfo")
                        deviceRepo.updateDeviceName(nearbyRegistered.map { it.deviceId to it.name })
                        nearbyRegistered.forEach { device ->
                            // 回写 RSSI 辅助 eviction 策略（仅对已有注册项的设备生效）
                            deviceRegistry.updateRssi(device.deviceId, device.rssi)
                            // 触发连接（ConnectionManager 内部去重，幂等安全）
                            connectionManager.requestConnect(device.deviceId)
                        }
                    }
                }

                Timber.tag(TAG).d("Scan burst end, pausing...")
                // ── 4. 冷却后进入下一轮 ─────────────────────────────────────────
                BluetoothManager.ensureBleScanInterval()
            }
        }
    }
}
