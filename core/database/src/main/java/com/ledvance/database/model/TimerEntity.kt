package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.command.common.TimerType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26
 * Describe : TimerEntity — 设备定时器数据库实体
 *
 * 表 device_timers 复合主键 (device_id, timer_type)，
 * 每台设备固定两条记录：ON（定时开灯）、OFF（定时关灯）。
 *
 * week_cycle: 协议原始值，0xFF = Never（不重复），按位表示每周重复星期。
 */
@Entity(
    tableName = "device_timers",
    primaryKeys = ["device_id", "timer_type"],
    foreignKeys = [
        ForeignKey(
            entity = DeviceEntity::class,
            parentColumns = ["device_id"],
            childColumns = ["device_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TimerEntity(
    @ColumnInfo(name = "device_id")
    val deviceId: DeviceId,

    @ColumnInfo(name = "timer_type")
    val timerType: TimerType,

    @ColumnInfo(name = "enabled")
    val enabled: Boolean = false,

    @ColumnInfo(name = "hour")
    val hour: Int = 0,

    @ColumnInfo(name = "minute")
    val minute: Int = 0,

    /** 协议原始 weekCycle 值，0xFF = Never */
    @ColumnInfo(name = "week_cycle")
    val weekCycle: Int = 0xFF,
)
