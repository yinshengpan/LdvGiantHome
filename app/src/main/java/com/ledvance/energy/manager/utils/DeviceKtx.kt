package com.ledvance.energy.manager.utils

import com.ledvance.ble.bean.Handshake
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.database.model.BoxType
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.NetworkMode

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 16:12
 * Describe : DeviceKtx
 */
fun Handshake.toDevice(): DeviceEntity = DeviceEntity(
    address = address,
    name = name,
    sn = sn,
    user = user,
    userId = userId,
    l1 = "0",
    l2 = "0",
    tripCurrent = "0",
    chargeCurrent = "0",
    scheduleStartTime = scheduleStartTime,
    scheduleChargeCurrent = scheduleChargeCurrent,
    minChargeCurrent = minChargeCurrent,
    maxChargeCurrent = maxChargeCurrent,
    firmwareVersion = firmwareVersion,
    bleVersion = bleVersion,
    chargeCount = chargeCount,
    faultCount = faultCount,
    plugAndChargeEnabled = plugAndChargeEnabled,
    networkMode = NetworkMode.fromType(networkMode),
    boxType = BoxType.fromType(boxType),
    index = System.currentTimeMillis()
)

fun DeviceEntity.toScannedDevice(): ScannedDevice = ScannedDevice(
    address = address,
    sn = sn,
    name = name
)