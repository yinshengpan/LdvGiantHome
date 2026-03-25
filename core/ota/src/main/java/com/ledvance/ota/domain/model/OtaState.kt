package com.ledvance.ota.domain.model

import android.bluetooth.BluetoothDevice

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/26/26 13:05
 * Describe : OtaState
 */
sealed interface OtaState {
    data class DeviceFound(val device: BluetoothDevice) : OtaState
    data object DeviceInfoUpdated : OtaState
    data class ConnectSuccess(val device: BluetoothDevice) : OtaState
    data object ModeSetSuccess : OtaState
    data object ModeSetFail : OtaState
    data object BeaconSuccess : OtaState
    data class OtaFail(val error: String) : OtaState
    data class OtaProgress(val progress: Float) : OtaState
    data object OtaSuccess : OtaState
}