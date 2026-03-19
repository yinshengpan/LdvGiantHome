package com.ledvance.light.bean

import com.ledvance.ui.component.IRadioGroupItem

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:17
 * Describe : ModeSegment
 */
enum class ModeSegment(override val title: String, override val value: String) : IRadioGroupItem<String> {
    Base("Base", "Base"),
    Curtain("Curtain", "Curtain"),
    Transition("Transition", "Transition"),
    ;

    companion object {
        val allModeSegment = listOf(
            Base,
            Curtain,
            Transition,
        )
    }
}