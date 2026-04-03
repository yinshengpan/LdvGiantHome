package com.ledvance.database.repo

import com.ledvance.database.dao.DeviceDao
import com.ledvance.database.dao.DeviceRuntimeConfigDao
import com.ledvance.database.model.DeviceBaseUpdateEntity
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.DeviceNameUpdateEntity
import com.ledvance.database.model.DevicePowerUpdateEntity
import com.ledvance.database.model.DeviceRuntimeConfigEntity
import com.ledvance.database.model.DeviceWithRuntimeConfig
import com.ledvance.domain.FirmwareVersion
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.giant.LineSequence
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.domain.bean.command.common.ModeType
import com.ledvance.utils.extensions.tryCatch
import com.ledvance.utils.extensions.tryCatchReturn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 09:49
 * Describe : DeviceRepo
 */
class DeviceRepo @Inject constructor(
    private val deviceDao: DeviceDao,
    private val deviceRuntimeConfigDao: DeviceRuntimeConfigDao
) {
    companion object {
        private const val TAG = "DeviceRepo"
    }

    suspend fun addDevice(deviceEntity: DeviceEntity) = withContext(Dispatchers.IO) {
        Timber.tag(TAG).d("addDevice: deviceId=%s", deviceEntity.deviceId)
        tryCatch {
            deviceDao.insert(deviceEntity)
            deviceRuntimeConfigDao.insertConfig(DeviceRuntimeConfigEntity(deviceId = deviceEntity.deviceId))
        }
    }

    suspend fun deleteDevice(deviceId: DeviceId) = withContext(Dispatchers.IO) {
        Timber.tag(TAG).d("deleteDevice: deviceId=%s", deviceId)
        tryCatch { deviceDao.deleteDevice(deviceId) }
    }

    suspend fun updateDevice(deviceEntity: DeviceEntity) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.update(deviceEntity) }
    }

    suspend fun updateDevicePower(deviceId: DeviceId, power: Boolean) = withContext(Dispatchers.IO) {
        Timber.tag(TAG).d("updateDevicePower: deviceId=%s, power=%s", deviceId, power)
        tryCatch { deviceDao.updateDevicePower(deviceId, power) }
    }

    suspend fun updateDeviceHs(deviceId: DeviceId, h: Int, s: Int) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateDeviceHs(deviceId, h, s) }
    }

    suspend fun updateDeviceV(deviceId: DeviceId, v: Int) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateDeviceV(deviceId, v) }
    }

    suspend fun updateDeviceCct(deviceId: DeviceId, cct: Int) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateDeviceCct(deviceId, cct) }
    }

    suspend fun updateDeviceBrightness(deviceId: DeviceId, brightness: Int) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateDeviceBrightness(deviceId, brightness) }
    }

    suspend fun updateDeviceSpeed(deviceId: DeviceId, speed: Int) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateDeviceSpeed(deviceId, speed) }
    }

    suspend fun updateDeviceWorkMode(deviceId: DeviceId, workMode: WorkMode) = withContext(Dispatchers.IO) {
        Timber.tag(TAG).d("updateDeviceWorkMode: deviceId=%s, workMode=%s", deviceId, workMode)
        tryCatch { deviceRuntimeConfigDao.updateDeviceWorkMode(deviceId, workMode) }
    }

    suspend fun updateDeviceLineSequence(deviceId: DeviceId, lineSequence: LineSequence) = withContext(Dispatchers.IO) {
        Timber.tag(TAG).d("updateDeviceLineSequence: deviceId=%s, lineSequence=%s", deviceId, lineSequence)
        tryCatch { deviceRuntimeConfigDao.updateDeviceLineSequence(deviceId, lineSequence) }
    }

    suspend fun updateDeviceMode(deviceId: DeviceId, modeType: ModeType?, modeId: ModeId?) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateModeId(deviceId, modeType, modeId) }
    }

    suspend fun updatePhoneMicSensitivity(deviceId: DeviceId, phoneMicSensitivity: Int) = withContext(Dispatchers.IO) {
        Timber.tag(TAG).d("updatePhoneMicSensitivity: deviceId=%s, sensitivity=%s", deviceId, phoneMicSensitivity)
        tryCatch { deviceRuntimeConfigDao.updatePhoneMicSensitivity(deviceId, phoneMicSensitivity) }
    }

    suspend fun updateDeviceFirmwareVersion(deviceId: DeviceId, deviceName: String, firmwareVersion: FirmwareVersion) =
        withContext(Dispatchers.IO) {
            tryCatch { deviceDao.updateDeviceFirmwareVersion(deviceId, deviceName, firmwareVersion) }
        }

    suspend fun updateDeviceName(list: List<Pair<DeviceId, String>>) = withContext(Dispatchers.IO) {
        tryCatch {
            val stateList = list.map { (deviceId, name) ->
                DeviceNameUpdateEntity(deviceId, name)
            }
            deviceDao.updateDeviceNameList(stateList)
        }
    }

    suspend fun updateDevicePower(list: List<Pair<DeviceId, Boolean>>) = withContext(Dispatchers.IO) {
        tryCatch {
            val stateList = list.map { (deviceId, power) ->
                DevicePowerUpdateEntity(deviceId, power)
            }
            deviceDao.updateDevicePowerList(stateList)
        }
    }

    suspend fun syncBaseInfoList(list: List<DeviceBaseUpdateEntity>) = withContext(Dispatchers.IO) {
        tryCatch {
            deviceDao.updateBaseInfoList(list)
        }
    }

    suspend fun getDevice(deviceId: DeviceId) = withContext(Dispatchers.IO) {
        return@withContext tryCatchReturn { deviceDao.getDevice(deviceId) }
    }

    fun getDeviceListFlow(): Flow<List<DeviceWithRuntimeConfig>> {
        return deviceDao.getDeviceListFlow().catch { }
    }

    fun getDeviceIdListFlow(): Flow<List<DeviceId>> {
        return deviceDao.getDeviceIdListFlow().catch { }
    }

    fun getDeviceFlow(deviceId: DeviceId): Flow<DeviceWithRuntimeConfig?> {
        return deviceDao.getDeviceFlow(deviceId).catch { }
    }

}