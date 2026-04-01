package com.ledvance.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ledvance.database.model.TimerEntity
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.command.common.TimerType
import kotlinx.coroutines.flow.Flow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26
 * Describe : TimerDao
 */
@Dao
interface TimerDao {

    /** 插入或替换（BLE 同步时调用） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTimer(timer: TimerEntity)

    /** 批量替换（一次同步两条） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTimers(timers: List<TimerEntity>)

    /** 查询单个定时器，UI 实时观察 */
    @Query("SELECT * FROM device_timers WHERE device_id = :deviceId AND timer_type = :timerType")
    fun getTimerFlow(deviceId: DeviceId, timerType: TimerType): Flow<TimerEntity?>

    /** 查询设备的全部定时器（ON + OFF） */
    @Query("SELECT * FROM device_timers WHERE device_id = :deviceId")
    fun getTimersFlow(deviceId: DeviceId): Flow<List<TimerEntity>>

    /** 一次性查询（非 Flow）*/
    @Query("SELECT * FROM device_timers WHERE device_id = :deviceId")
    suspend fun getTimers(deviceId: DeviceId): List<TimerEntity>

    @Query("DELETE FROM device_timers WHERE device_id = :deviceId")
    suspend fun deleteTimers(deviceId: DeviceId)
}
