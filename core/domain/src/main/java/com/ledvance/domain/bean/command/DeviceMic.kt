package com.ledvance.domain.bean.command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:25
 * Describe : DeviceMic
 */
enum class DeviceMic(val title: String, override val command: Byte) : Command {
    Energy1("Energy 1", 0x01),
    Energy2("Energy 2", 0x02),
    Rhythm1("Rhythm 1", 0x03),
    Rhythm2("Rhythm 2", 0x04),
    Spectrum1("Spectrum 1", 0x05),
    Spectrum2("Spectrum 2", 0x06),
    Roll1("Roll 1", 0x07),
    Roll2("Roll 2", 0x08),
    ;

    companion object {
        val items = DeviceMic.entries.toList()
    }
}