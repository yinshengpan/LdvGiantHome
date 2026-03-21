package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.ModeType
import com.ledvance.domain.bean.command.LineSequence

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
    @ColumnInfo(name = "work_mode")
    val workMode: WorkMode = WorkMode.Colour,
    @ColumnInfo(name = "power")
    val power: Boolean = true,
    @ColumnInfo(name = "line_sequence")
    val lineSequence: LineSequence = LineSequence.RGB,
    @ColumnInfo(name = "mode_type")
    val modeType: ModeType? = null,
    @ColumnInfo(name = "mode_id")
    val modeId: ModeId? = null,
    @ColumnInfo(name = "speed")
    val speed: Int = 100,
    @ColumnInfo(name = "h")
    val h: Int = 360,
    @ColumnInfo(name = "s")
    val s: Int = 100,
    @ColumnInfo(name = "v")
    val v: Int = 100,
    @ColumnInfo(name = "cct")
    val cct: Int = 100,
    @ColumnInfo(name = "brightness")
    val brightness: Int = 100,
    @ColumnInfo(name = "firmware_version")
    val firmwareVersion: String = "",
)