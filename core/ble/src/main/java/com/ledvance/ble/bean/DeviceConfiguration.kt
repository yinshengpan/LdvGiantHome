package com.ledvance.ble.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 11:39
 * Describe : DeviceConfiguration
 */
sealed class DeviceConfiguration(val name: String) {
    data object TripCurrent : DeviceConfiguration("dlb")
    data object WhiteList : DeviceConfiguration("white")
    data object L1 : DeviceConfiguration("CurL1")
    data object L2 : DeviceConfiguration("CurL2")
    data object L3 : DeviceConfiguration("CurL3")
    data class ChargeCurrent(val chargeNumber: String) : DeviceConfiguration("Cur[$chargeNumber]")
}

enum class WhiteListState(val value: Int) {
    Disabled(0),
    Enabled(1)
}