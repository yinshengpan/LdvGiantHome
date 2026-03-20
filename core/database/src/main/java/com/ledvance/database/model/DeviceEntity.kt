package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 17:57
 * Describe : DeviceEntity — 字段对齐蓝牙协议
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    val deviceId: DeviceId,
    val name: String,
    @ColumnInfo(name = "device_type")
    val deviceType: DeviceType,
    @ColumnInfo(name = "power")
    val power: Boolean = true,
    @ColumnInfo(name = "mode_type")
    val modeType: Int = 1,
    @ColumnInfo(name = "mode")
    val mode: Int = 0,
    @ColumnInfo(name = "brightness")
    val brightness: Int = 100,
    @ColumnInfo(name = "speed")
    val speed: Int = 100,
    @ColumnInfo(name = "r")
    val r: Int = 255,
    @ColumnInfo(name = "g")
    val g: Int = 255,
    @ColumnInfo(name = "b")
    val b: Int = 255,
    @ColumnInfo(name = "w")
    val w: Int = 0,
)