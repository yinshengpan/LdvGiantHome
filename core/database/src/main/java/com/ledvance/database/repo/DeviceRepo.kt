package com.ledvance.database.repo

import com.ledvance.database.dao.DeviceDao
import com.ledvance.database.model.DeviceBaseUpdateEntity
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.DevicePowerUpdateEntity
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.LineSequence
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.ModeType
import com.ledvance.utils.extensions.tryCatch
import com.ledvance.utils.extensions.tryCatchReturn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 09:49
 * Describe : DeviceRepo
 */
class DeviceRepo @Inject constructor(
    private val deviceDao: DeviceDao
) {

    suspend fun addDevice(deviceEntity: DeviceEntity) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.insert(deviceEntity) }
    }

    suspend fun deleteDevice(deviceId: DeviceId) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.deleteDevice(deviceId) }
    }

    suspend fun updateDevice(deviceEntity: DeviceEntity) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.update(deviceEntity) }
    }

    suspend fun updateDevicePower(deviceId: DeviceId, power: Boolean) = withContext(Dispatchers.IO) {
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
        tryCatch { deviceDao.updateDeviceWorkMode(deviceId, workMode) }
    }

    suspend fun updateDeviceLineSequence(deviceId: DeviceId, lineSequence: LineSequence) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateDeviceLineSequence(deviceId, lineSequence) }
    }

    suspend fun updateDeviceMode(deviceId: DeviceId, modeType: ModeType?, modeId: ModeId?) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateModeId(deviceId, modeType, modeId) }
    }

    suspend fun updateDeviceFirmwareVersion(deviceId: DeviceId, firmwareVersion: String) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateDeviceFirmwareVersion(deviceId, firmwareVersion) }
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

    fun getDeviceListFlow(): Flow<List<DeviceEntity>> {
        return deviceDao.getDeviceListFlow().catch { }
    }

    fun getDeviceIdListFlow(): Flow<List<DeviceId>> {
        return deviceDao.getDeviceIdListFlow().catch { }
    }

    fun getDeviceFlow(deviceId: DeviceId): Flow<DeviceEntity?> {
        return deviceDao.getDeviceFlow(deviceId).catch { }
    }

}