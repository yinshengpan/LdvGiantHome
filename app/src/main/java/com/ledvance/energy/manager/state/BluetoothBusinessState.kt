package com.ledvance.energy.manager.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/11 14:49
 * Describe : BluetoothBusinessState
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberBluetoothBusinessState(): BluetoothBusinessState {
    val locationEnable by rememberLocationEnableState()
    val bluetoothEnable by rememberBluetoothEnableState()
    val bluetoothPermissions by rememberBluetoothPermissionState()
    val bluetoothBusinessState = remember(locationEnable, bluetoothEnable, bluetoothPermissions) {
        MutableBluetoothBusinessState(locationEnable, bluetoothEnable, bluetoothPermissions)
    }
    return bluetoothBusinessState
}

interface BluetoothBusinessState : State<BluetoothBusinessState> {
    fun hasAllow(): Boolean
}

private class MutableBluetoothBusinessState @OptIn(ExperimentalPermissionsApi::class) constructor(
    private val locationEnableState: EnableState,
    private val bluetoothEnableState: EnableState,
    private val bluetoothPermissionState: BluetoothPermissionState
) : BluetoothBusinessState {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun hasAllow(): Boolean {
        return when {
            !locationEnableState.hasEnable() -> {
                false
            }

            !bluetoothPermissionState.hasGranted() -> {
                false
            }

            !bluetoothEnableState.hasEnable() -> {
                false
            }

            else -> true
        }
    }

    override val value: BluetoothBusinessState
        get() = this

}