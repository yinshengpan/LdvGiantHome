package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.WorkMode

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 17:57
 * Describe : DeviceEntity
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val address: String,
    val name: String,
    @ColumnInfo(name = "device_type")
    val deviceType: DeviceType,
    @ColumnInfo(name = "switch_state")
    val switch: Boolean = true,
    @ColumnInfo(name = "work_mode")
    val workMode: WorkMode = WorkMode.Colour,
    val hue: Int = 360,
    val sat: Int = 100,
    val value: Int = 100,
    val cct: Int = 100,
    val brightness: Int = 100,
    val speed: Int = 100,
)