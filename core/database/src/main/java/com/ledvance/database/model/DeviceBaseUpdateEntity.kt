package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.ledvance.domain.bean.DeviceId

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 14:38
 * Describe : DeviceBaseUpdateEntity — 基础状态局部更新实体
 */
data class DeviceBaseUpdateEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    val deviceId: DeviceId,
    @ColumnInfo(name = "power")
    val power: Boolean,
    @ColumnInfo(name = "mode_type")
    val modeType: Int,
    @ColumnInfo(name = "mode")
    val mode: Int,
    @ColumnInfo(name = "brightness")
    val brightness: Int,
    @ColumnInfo(name = "speed")
    val speed: Int,
    @ColumnInfo(name = "r")
    val r: Int,
    @ColumnInfo(name = "g")
    val g: Int,
    @ColumnInfo(name = "b")
    val b: Int,
    @ColumnInfo(name = "w")
    val w: Int,
)
