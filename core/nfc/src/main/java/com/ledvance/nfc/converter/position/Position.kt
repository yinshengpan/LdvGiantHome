package com.ledvance.nfc.converter.position

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/6 13:49
 * Describe : Position
 */
internal open class Position(
    val startIndex: Int = 0,
    val endIndex: Int = 0,
) {
    companion object {
        val Default = Position()
    }
}