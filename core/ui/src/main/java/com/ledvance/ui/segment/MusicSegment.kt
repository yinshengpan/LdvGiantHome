package com.ledvance.ui.segment

import com.ledvance.ui.R
import com.ledvance.ui.component.IRadioGroupItem
import com.ledvance.utils.extensions.getString

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:17
 * Describe : MusicSegment
 */
enum class MusicSegment(override val title: String, override val value: String) : IRadioGroupItem<String> {
    DeviceMic(getString(R.string.music_device_mic), "DeviceMic"),
    PhoneMic(getString(R.string.music_phone_mic), "PhoneMic"),
    Music(getString(R.string.title_music), "Music"),
    ;

    companion object {
        val allMusicSegment = listOf(
            DeviceMic,
            PhoneMic,
            Music,
        )
    }
}
