package com.ledvance.database.repo

import com.ledvance.database.dao.FirmwareLatestDao
import com.ledvance.database.model.FirmwareLatestEntity
import com.ledvance.domain.FirmwareLatest
import com.ledvance.domain.bean.DeviceType
import com.ledvance.utils.extensions.tryCatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/25/26 17:59
 * Describe : FirmwareLatestRepo
 */
class FirmwareLatestRepo @Inject constructor(
    private val firmwareLatestDao: FirmwareLatestDao
) {

    suspend fun addFirmwareLatest(firmwareLatest: FirmwareLatest) = withContext(Dispatchers.IO) {
        tryCatch {
            firmwareLatestDao.upsertFirmwareLatest(firmwareLatest.toFirmwareLatestEntity())
        }
    }

    suspend fun addFirmwareLatestList(firmwareLatestList: List<FirmwareLatest>) = withContext(Dispatchers.IO) {
        tryCatch {
            firmwareLatestDao.upsertFirmwareLatestList(firmwareLatestList.map {
                it.toFirmwareLatestEntity()
            })
        }
    }

    fun getFirmwareLatestFlow(deviceType: DeviceType): Flow<FirmwareLatest?> =
        firmwareLatestDao.getFirmwareLatestFlow(deviceType)
            .map { it?.toDomain() }
            .catch { }


    private fun FirmwareLatestEntity.toDomain() = FirmwareLatest(
        deviceType = deviceType,
        latestVersion = latestVersion,
        firmwareFilePath = firmwareFilePath,
        firmwareFileSize = firmwareFileSize,
        firmwareMd5 = firmwareMd5,
        firmwareUrl = firmwareUrl,
    )

    private fun FirmwareLatest.toFirmwareLatestEntity() = FirmwareLatestEntity(
        deviceType = deviceType,
        latestVersion = latestVersion,
        firmwareFilePath = firmwareFilePath,
        firmwareFileSize = firmwareFileSize,
        firmwareMd5 = firmwareMd5,
        firmwareUrl = firmwareUrl,
    )

}