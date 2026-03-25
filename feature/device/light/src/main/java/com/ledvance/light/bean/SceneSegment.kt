package com.ledvance.light.bean

import com.ledvance.domain.bean.command.scenes.TableSceneType
import com.ledvance.ui.R
import com.ledvance.ui.component.IRadioGroupItem
import com.ledvance.utils.extensions.getString

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 17:40
 * Describe : SceneSegment
 */
internal enum class SceneSegment(override val title: String, override val value: TableSceneType) :
    IRadioGroupItem<TableSceneType> {
    Natural(getString(R.string.scene_natural), value = TableSceneType.Natural),
    Festival(getString(R.string.scene_festival), value = TableSceneType.Festival),
    Soothing(getString(R.string.scene_soothing), value = TableSceneType.Soothing),
    Life(getString(R.string.scene_life), value = TableSceneType.Life),
    ;

    companion object {
        val allSceneSegment = listOf(
            Natural,
            Festival,
            Soothing,
            Life,
        )
    }
}