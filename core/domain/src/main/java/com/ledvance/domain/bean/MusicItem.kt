package com.ledvance.domain.bean

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
    val fileName: String
) {
    companion object {
        val allMusicItems = listOf(
            MusicItem(1, "水色", "Deep Forest-晨", "Deep Forest - 水色.mp3"),
            MusicItem(2, "月光の雲海 (月光下的云海)", "iw ix-极品钢琴 游戏原声带", "iw ix - 月光の雲海 (月光下的云海).mp3"),
            MusicItem(3, "a bird lived", "July-July", "July - a bird lived.mp3"),
            MusicItem(4, "Fallen Leaves Sound", "July-null", "July - Fallen Leaves Sound.mp3"),
            MusicItem(5, "To Heaven", "July-To Heaven", "July - To Heaven.mp3"),
            MusicItem(6, "Truth", "July-Tune On Memories", "July - Truth.mp3"),
            MusicItem(7, "Unknown Place", "July-Unknown Place", "July - Unknown Place.mp3"),
            MusicItem(8, "Walking to Heaven", "July-In Love", "July - Walking to Heaven.mp3"),
            MusicItem(9, "Matrimonio De Amor (梦中的婚礼)", "Richard Clayderman", "Richard Clayderman - Matrimonio De Amor (梦中的婚礼).mp3"),
            MusicItem(10, "いつも何度でも (永远同在)", "宗次郎", "宗次郎 - いつも何度でも (永远同在).mp3")
        )
    }
}
