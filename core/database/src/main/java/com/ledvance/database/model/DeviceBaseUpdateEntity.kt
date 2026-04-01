package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.domain.bean.command.giant.ModeType

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
    val modeType: ModeType? = null,
    @ColumnInfo(name = "mode_id")
    val modeId: ModeId? = null,
    @ColumnInfo(name = "speed")
    val speed: Int,
    @ColumnInfo(name = "h")
    val h: Int,
    @ColumnInfo(name = "s")
    val s: Int,
    @ColumnInfo(name = "v")
    val v: Int,
    @ColumnInfo(name = "cct")
    val cct: Int,
    @ColumnInfo(name = "brightness")
    val brightness: Int,
)
