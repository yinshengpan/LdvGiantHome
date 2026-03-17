package com.ledvance.database.repo

import com.ledvance.database.dao.SetTripCurrentHistoryDao
import com.ledvance.database.model.SetTripCurrentHistoryEntity
import com.ledvance.database.model.SetTripCurrentType
import com.ledvance.utils.extensions.tryCatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 17:28
 * Describe : SetTripCurrentHistoryRepo
 */
class SetTripCurrentHistoryRepo @Inject constructor(
    private val setTripCurrentHistoryDao: SetTripCurrentHistoryDao
) {
    suspend fun addHistory(sn: String, tripCurrent: Int, type: SetTripCurrentType) =
        withContext(Dispatchers.IO) {
            tryCatch {
                val history = SetTripCurrentHistoryEntity(
                    sn = sn,
                    tripCurrent = tripCurrent,
                    type = type,
                    createTime = System.currentTimeMillis()
                )
                setTripCurrentHistoryDao.insert(history)
            }
        }

    fun getHistoryListFlow(): Flow<List<SetTripCurrentHistoryEntity>> {
        return setTripCurrentHistoryDao.getHistoryListFlow().catch { }
    }
}