package com.ledvance.domain.bean

import com.ledvance.domain.R

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/19 20:38
 * Describe : MusicItem
 */
data class MusicItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val resId: Int
) {
    companion object {
        val allMusicItems = listOf(
            MusicItem(1, "a bird lived", "July - July", 1),
            MusicItem(2, "Fallen Leaves Sound", "- July", 2),
            MusicItem(3, "To Heaven", "To Heaven - July", 3),
            MusicItem(4, "Truth", "Tune On Memories - July", 4),
            MusicItem(5, "Unknown Place", "Unknown Place - July", 5),
            MusicItem(6, "Walking to Heaven", "In Love - July", 6),
            MusicItem(7, "City Lights", "Downtown - July", 7),
            MusicItem(8, "Ocean Breeze", "Seaside - July", 8),
            MusicItem(9, "Mountain High", "Summit - July", 9),
            MusicItem(10, "Forest Whisper", "Woodland - July", 10)
        )
    }
}
