package com.ledvance.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.SystemClock
import com.ledvance.connected.system.extensions.openBluetooth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/28 14:16
 * Describe : BluetoothManager
 */
object BluetoothManager {
    private const val TAG = "BluetoothManager"
    private val bluetoothAdapter by lazy {
        (AppContext.get().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
    private var lastScanBleDeviceTime: Long = 0L
    private val bluetoothEnableFlow = MutableStateFlow(false)
    val bluetoothEnableState: StateFlow<Boolean> = bluetoothEnableFlow

    fun initialize() {
        bluetoothEnableFlow.tryEmit(bluetoothAdapter.state.hasBluetoothEnable())
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        AppContext.get().registerReceiver(bluetoothReceiver, filter)
    }

    @SuppressLint("MissingPermission")
    fun openBluetooth() {
        AppContext.get().openBluetooth()
    }

    fun isBluetoothEnable() = bluetoothEnableFlow.value

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.tag(TAG).d("bluetoothReceiver action -> ${intent?.action}")
            when (intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state =
                        intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    val isEnable = state.hasBluetoothEnable()
                    Timber.tag(TAG)
                        .i("bluetoothReceiver switchStateValue=$state , switchState=$isEnable")
                    bluetoothEnableFlow.tryEmit(isEnable)
                }

                else -> {

                }
            }
        }
    }

    private fun Int.hasBluetoothEnable(): Boolean {
        return BluetoothAdapter.STATE_ON == this
    }

    suspend fun ensureBleScanInterval() {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        val timeDifference = elapsedRealtime - lastScanBleDeviceTime
        // Android7.0系统以上30秒内不能超过5次扫描,所以限制6秒内只能扫一次，不足6秒做等待
        if (timeDifference < 6000L) {
            delay(6000L - timeDifference)
        }
        lastScanBleDeviceTime = elapsedRealtime
    }
}