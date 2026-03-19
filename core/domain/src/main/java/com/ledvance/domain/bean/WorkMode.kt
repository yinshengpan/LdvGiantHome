package com.ledvance.domain.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/9/11 10:58
 * Describe : WorkMode
 */
enum class WorkMode(val value: String) {
    White("white"),
    Colour("colour"),
    Scene("scene"),
    Music("music"),
    ;

    companion object {
        fun of(value: String): WorkMode {
            return entries.find { it.value == value } ?: Colour
        }
    }
}