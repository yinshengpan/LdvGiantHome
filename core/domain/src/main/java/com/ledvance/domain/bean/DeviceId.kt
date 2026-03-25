package com.ledvance.domain.bean

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.getSystemService
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 10:53
 * Describe : DeviceId
 */

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class DeviceId(val macAddress: String, val deviceType: DeviceType) {
    override fun toString() = macAddress
}

fun DeviceId.asBluetoothDevice(context: Context): BluetoothDevice? {
    return try {
        if (!BluetoothAdapter.checkBluetoothAddress(macAddress)) {
            return null
        }
        val adapter = context.getSystemService<BluetoothManager>()?.adapter ?: return null
        adapter.getRemoteDevice(macAddress)
    } catch (e: Throwable) {
        null
    }
}

