package com.ledvance.energy.manager.model

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/10/10 11:02
 * Describe : Language
 */

sealed class Language(val name: String, val tag: String) {
    object English : Language("English", "en")
    object Spanish : Language("Español", "es")
    object French : Language("Français", "fr")
    object Portuguese : Language("Português", "pt")
}