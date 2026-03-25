package com.ledvance.domain.bean.command

import com.ledvance.domain.bean.DeviceType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:25
 * Describe : DeviceMicRhythm
 */
enum class DeviceMicRhythm(override val command: Byte) : Command {
    Energy(0x01),      // 能量模式1
    Energy1(0x01),      // 能量模式1
    Energy2(0x02),      // 能量模式2
    Rhythm(0x03),      // 律动模式1
    Rhythm1(0x03),      // 律动模式1
    Rhythm2(0x04),      // 律动模式2
    Spectrum1(0x05),    // 频谱模式1
    Spectrum2(0x06),    // 频谱模式2
    Roll1(0x07),        // 滚动模式1
    Roll2(0x08);        // 滚动模式2

    companion object {
        val itemsMap = mapOf(
            DeviceType.GiantTable to listOf(
                Energy1,
                Energy2,
                Rhythm1,
                Rhythm2,
                Spectrum1,
                Spectrum2,
                Roll1,
                Roll2
            ),
            DeviceType.GiantFloor to listOf(
                Energy,
                Rhythm,
                Spectrum1,
                Spectrum2,
                Roll1,
                Roll2
            ),
        )
    }
}