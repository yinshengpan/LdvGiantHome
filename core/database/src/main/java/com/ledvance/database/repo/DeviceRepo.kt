package com.ledvance.database.repo

import com.ledvance.database.dao.DeviceDao
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.DeviceSwitchUpdateEntity
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

    suspend fun deleteDevice(address: String) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.deleteDevice(address) }
    }

    suspend fun updateDevice(deviceEntity: DeviceEntity) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.update(deviceEntity) }
    }

    suspend fun updateDeviceSwitch(address: String, switch: Boolean) = withContext(Dispatchers.IO) {
        tryCatch { deviceDao.updateDeviceSwitch(address, switch) }
    }

    suspend fun updateDeviceSwitch(list: List<Pair<String, Boolean>>) = withContext(Dispatchers.IO) {
        tryCatch {
            val stateList = list.map { (address, switch) -> DeviceSwitchUpdateEntity(address, switch) }
            deviceDao.updateDeviceSwitchList(stateList)
        }
    }

    suspend fun getDevice(address: String) = withContext(Dispatchers.IO) {
        return@withContext tryCatchReturn { deviceDao.getDevice(address) }
    }

    fun getDeviceListFlow(): Flow<List<DeviceEntity>> {
        return deviceDao.getDeviceListFlow().catch { }
    }

    fun getDeviceIdListFlow(): Flow<List<String>> {
        return deviceDao.getDeviceIdListFlow().catch { }
    }

    fun getDeviceFlow(address: String): Flow<DeviceEntity?> {
        return deviceDao.getDeviceFlow(address).catch { }
    }

}