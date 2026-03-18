package com.ledvance.energy.manager.utils

import com.ledvance.ble.bean.Handshake
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.database.model.DeviceEntity

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 16:12
 * Describe : DeviceKtx
 */
fun DeviceEntity.toScannedDevice(): ScannedDevice = ScannedDevice(
    address = address,
    name = name
)