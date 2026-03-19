package com.ledvance.light.bean

import com.ledvance.domain.bean.command.scenes.TableSceneType
import com.ledvance.ui.component.IRadioGroupItem

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 17:40
 * Describe : ScenesSegment
 */
enum class ScenesSegment(override val title: String, override val value: TableSceneType) :
    IRadioGroupItem<TableSceneType> {
    Natural("Natural", value = TableSceneType.Natural),
    Festival("Festival", value = TableSceneType.Festival),
    Soothing("Soothing", value = TableSceneType.Soothing),
    Life("Life", value = TableSceneType.Life),
    ;

    companion object {
        val allScenesSegment = listOf(
            Natural,
            Festival,
            Soothing,
            Life,
        )
    }
}