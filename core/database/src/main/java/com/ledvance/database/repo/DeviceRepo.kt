package com.ledvance.database.repo

import com.ledvance.database.dao.DeviceDao
import com.ledvance.database.model.DeviceEntity
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

    suspend fun getDevice(address: String) = withContext(Dispatchers.IO) {
        return@withContext tryCatchReturn { deviceDao.getDevice(address) }
    }

    suspend fun getSN(address: String): String? = withContext(Dispatchers.IO) {
        return@withContext tryCatchReturn { deviceDao.getSN(address) }
    }

    fun getDeviceListFlow(): Flow<List<DeviceEntity>> {
        return deviceDao.getDeviceListFlow().catch { }
    }

    fun getDeviceFlow(address: String): Flow<DeviceEntity?> {
        return deviceDao.getDeviceFlow(address).catch { }
    }

    suspend fun updateTripCurrent(address: String, tripCurrent: Int) = withContext(Dispatchers.IO) {
        return@withContext tryCatch {
            deviceDao.updateTripCurrent(address, tripCurrent)
        }
    }

}