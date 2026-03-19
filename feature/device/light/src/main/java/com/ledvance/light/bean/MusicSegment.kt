package com.ledvance.light.bean

import com.ledvance.ui.component.IRadioGroupItem

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:17
 * Describe : ModeSegment
 */
enum class MusicSegment(override val title: String, override val value: String) : IRadioGroupItem<String> {
    DeviceMic("Device Mic", "DeviceMic"),
    PhoneMic("Phone Mic", "PhoneMic"),
    Music("Music", "Music"),
    ;

    companion object {
        val allMusicSegment = listOf(
            DeviceMic,
            PhoneMic,
            Music,
        )
    }
}