package com.ledvance.domain.bean.command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:25
 * Describe : DeviceMic
 */
enum class DeviceMic(val title: String, override val command: Byte) : Command {
    Energy("Energy", 0x01),
    Rhythm("Rhythm", 0x02),
    Spectrum1("Spectrum 1", 0x03),
    Spectrum2("Spectrum 2", 0x04),
    Roll1("Roll 1", 0x05),
    Roll2("Roll 2", 0x06),
    ;
}