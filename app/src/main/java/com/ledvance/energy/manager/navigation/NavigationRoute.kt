package com.ledvance.energy.manager.navigation

import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.energy.manager.model.License
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/19 13:36
 * Describe : NavigationRoute
 */
interface NavigationRoute

@Serializable
data class DeviceDetailRoute(val device: ScannedDevice) : NavigationRoute

@Serializable
data object DeviceListRoute : NavigationRoute

@Serializable
data object LanguageRoute : NavigationRoute

@Serializable
data object FirmwareUpdateRoute : NavigationRoute

@Serializable
data class QRCodeScanRoute(val device: ScannedDevice) : NavigationRoute

@Serializable
data object OpenSourceLicensesRoute : NavigationRoute

@Serializable
data class LicenseContentRoute(val license: License) : NavigationRoute

@Serializable
data class NFCDetectionRoute(val device: ScannedDevice) : NavigationRoute

@Serializable
data object SetHistoryRoute : NavigationRoute