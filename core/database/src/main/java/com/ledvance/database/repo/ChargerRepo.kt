package com.ledvance.database.repo

import com.ledvance.database.dao.ChargerDao
import com.ledvance.database.model.ChargerEntity
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
 * Created date 11/18/25 11:47
 * Describe : ChargerRepo
 */
class ChargerRepo @Inject constructor(
    private val chargerDao: ChargerDao
) {

    suspend fun deleteNotIn(
        address: String, isPaired: Boolean, existsChargerList: List<String>
    ) = withContext(Dispatchers.IO) {
        tryCatch { chargerDao.deleteNotIn(address, isPaired = isPaired, existsChargerList) }
    }

    suspend fun updateChargerStatus(chargerNumber: String, isPaired: Boolean) =
        withContext(Dispatchers.IO) {
            tryCatch { chargerDao.updateChargerStatus(chargerNumber, isPaired) }
        }

    suspend fun update(devices: List<ChargerEntity>) = withContext(Dispatchers.IO) {
        tryCatch { chargerDao.update(devices) }
    }

    suspend fun getChargerList(address: String, isPaired: Boolean): List<ChargerEntity> =
        withContext(Dispatchers.IO) {
            return@withContext tryCatchReturn { chargerDao.getChargerList(address, isPaired) }
                ?: listOf()
        }

    fun getAllChargerListFlow(address: String): Flow<List<ChargerEntity>> {
        return chargerDao.getAllChargerListFlow(address).catch { }
    }

    suspend fun updateChargerList(
        sn: String,
        address: String,
        isPaired: Boolean,
        chargerList: List<String>
    ) = withContext(Dispatchers.IO) {
        val localChargerList = tryCatchReturn { chargerDao.getAllChargerList(address) } ?: listOf()
        val localChargeCurrent = localChargerList.associate {
            it.chargerNumber to it.chargeCurrent
        }
        val chargerEntityList = chargerList.map {
            ChargerEntity(
                chargerNumber = it,
                boxAddress = address,
                boxSn = sn,
                chargeCurrent = localChargeCurrent[it] ?: "0",
                isPaired = isPaired,
                isOnline = isPaired,
            )
        }
        val localList = localChargerList.takeIf { isPaired }
            ?.filter { !chargerList.contains(it.chargerNumber) }
            ?.map { it.copy(isOnline = false) } ?: listOf()
        return@withContext tryCatch {
            chargerDao.insert(localList + chargerEntityList)
        }
    }
}