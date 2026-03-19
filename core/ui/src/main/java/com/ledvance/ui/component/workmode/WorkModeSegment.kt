package com.ledvance.ui.component.workmode

import com.ledvance.domain.bean.WorkMode
import com.ledvance.ui.component.IRadioGroupItem

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/9/5 15:22
 * Describe : WorkModeSegment
 */
enum class WorkModeSegment(override val title: String, override val value: WorkMode) :
    IRadioGroupItem<WorkMode> {
    ColorMode("Color mode", value = WorkMode.Colour),
    WhiteMode("White mode", value = WorkMode.White),
    ;

    companion object {
        val allWorkModeSegment = listOf(
            ColorMode,
            WhiteMode,
        )

        fun ofValue(value: String): WorkModeSegment {
            return entries.find { it.value.value == value } ?: ColorMode
        }

        fun ofWorkMode(value: WorkMode): WorkModeSegment {
            return entries.find { it.value == value } ?: ColorMode
        }
    }
}