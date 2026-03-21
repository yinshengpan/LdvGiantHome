package com.ledvance.usecase.device

import com.ledvance.ble.core.ConnectionManager
import com.ledvance.domain.bean.DeviceId
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : FirmwareUseCase
 */
@Singleton
class FirmwareUseCase @Inject constructor(
    private val connectionManager: ConnectionManager
) {
    private val TAG = "FirmwareUseCase"

    suspend fun readFirmwareVersion(deviceId: DeviceId): String? {
        return try {
            val client = connectionManager.getClient(deviceId)
            if (client == null || !client.isConnected) {
                Timber.tag(TAG).w("readFirmwareVersion: Device not connected")
                return null
            }
            client.readFirmwareVersion()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "readFirmwareVersion failed")
            null
        }
    }

    suspend fun updateFirmware(deviceId: DeviceId, firmwareData: ByteArray): Boolean {
        return try {
            val client = connectionManager.getClient(deviceId)
            if (client == null || !client.isConnected) {
                Timber.tag(TAG).w("updateFirmware: Device not connected")
                return false
            }
            client.updateFirmware(firmwareData)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "updateFirmware failed")
            false
        }
    }
}
