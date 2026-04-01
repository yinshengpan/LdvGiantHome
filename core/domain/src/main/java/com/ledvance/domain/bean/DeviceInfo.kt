package com.ledvance.domain.bean

import com.ledvance.domain.FirmwareVersion
import com.ledvance.domain.bean.command.giant.LineSequence
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.domain.bean.command.giant.ModeType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 15:11
 * Describe : DeviceInfo
 */
data class DeviceInfo(
    val deviceId: DeviceId,
    val deviceType: DeviceType,
    val workMode: WorkMode,
    val lineSequence: LineSequence,
    val name: String,
    val power: Boolean,
    val firmwareVersion: FirmwareVersion,
    val modeType: ModeType? = null,
    val modeId: ModeId? = null,
    val speed: Int = 50,
    val phoneMicSensitivity: Int = 60,
    val h: Int = 255,
    val s: Int = 255,
    val v: Int = 255,
    val cct: Int = 0,
    val brightness: Int = 100,
)