package com.ledvance.light.bean

import com.ledvance.domain.bean.command.ModeId
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
    FlowingWater("Flowing Water", "FlowingWater"),
    Flow("Flow", "Flow"),
    Tail("Tail", "Tail"),
    Run("Run", "Run"),
    RunBack("Run Back", "RunBack"),
    Other("Other", "Other"),
    ;

    companion object {
        val allModeSegment = listOf(
            Base,
            Curtain,
            Transition,
            FlowingWater,
            Flow,
            Tail,
            Run,
            RunBack,
            Other,
        )

        fun getModesBySegment(segment: ModeSegment): List<ModeId> {
            return when (segment) {
                Base -> ModeId.baseItems
                Curtain -> ModeId.curtainItems
                Transition -> ModeId.transitionItems
                FlowingWater -> ModeId.flowingWaterItems
                Flow -> ModeId.flowItems
                Tail -> ModeId.tailItems
                Run -> ModeId.runItems
                RunBack -> ModeId.runBackItems
                Other -> ModeId.otherItems
            }
        }
    }
}