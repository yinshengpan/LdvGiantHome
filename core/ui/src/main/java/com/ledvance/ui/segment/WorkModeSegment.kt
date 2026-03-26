package com.ledvance.ui.segment

import com.ledvance.domain.bean.WorkMode
import com.ledvance.ui.R
import com.ledvance.ui.component.IRadioGroupItem
import com.ledvance.utils.extensions.getString

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/9/5 15:22
 * Describe : WorkModeSegment
 */
enum class WorkModeSegment(override val title: String, override val value: WorkMode) :
    IRadioGroupItem<WorkMode> {
    ColorMode(getString(R.string.color_mode), value = WorkMode.Colour),
    WhiteMode(getString(R.string.white_mode), value = WorkMode.White),
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