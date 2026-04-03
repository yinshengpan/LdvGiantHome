package com.ledvance.database.repo

import com.ledvance.database.dao.TimerDao
import com.ledvance.database.model.TimerEntity
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceTimer
import com.ledvance.domain.bean.command.common.TimerType
import com.ledvance.utils.extensions.tryCatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26
 * Describe : TimerRepo — 定时器数据仓库
 */
class TimerRepo @Inject constructor(
    private val timerDao: TimerDao
) {

    suspend fun upsertTimer(timer: TimerEntity) = withContext(Dispatchers.IO) {
        tryCatch { timerDao.upsertTimer(timer) }
    }

    suspend fun upsertTimers(timers: List<TimerEntity>) = withContext(Dispatchers.IO) {
        tryCatch { timerDao.upsertTimers(timers) }
    }

    /** 观察指定定时器（供 UI 实时更新） */
    fun getTimerFlow(deviceId: DeviceId, timerType: TimerType): Flow<DeviceTimer?> =
        timerDao.getTimerFlow(deviceId, timerType)
            .map { it?.toDomain() }
            .catch { }

    /** 观察设备所有定时器 */
    fun getTimersFlow(deviceId: DeviceId): Flow<List<DeviceTimer>> =
        timerDao.getTimersFlow(deviceId)
            .map { it.map { it.toDomain() } }
            .distinctUntilChanged()
            .catch { emit(emptyList()) }

    /** 设备删除时级联清理 */
    suspend fun deleteTimers(deviceId: DeviceId) = withContext(Dispatchers.IO) {
        tryCatch { timerDao.deleteTimers(deviceId) }
    }

    // ---- mapping ----

    private fun TimerEntity.toDomain() = DeviceTimer(
        deviceId  = deviceId,
        timerType = timerType,
        enabled   = enabled,
        hour      = hour,
        minute    = minute,
        weekCycle = weekCycle,
        delay = delay
    )
}
