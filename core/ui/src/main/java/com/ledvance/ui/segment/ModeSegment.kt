package com.ledvance.ui.segment

import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.ui.R
import com.ledvance.ui.component.IRadioGroupItem
import com.ledvance.utils.extensions.getString

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:17
 * Describe : ModeSegment
 */
enum class ModeSegment(override val title: String, override val value: String) : IRadioGroupItem<String> {
    Base(getString(R.string.mode_base), "Base"),
    Curtain(getString(R.string.mode_curtain), "Curtain"),
    Transition(getString(R.string.mode_transition), "Transition"),
    FlowingWater(getString(R.string.mode_flowing_water), "FlowingWater"),
    Flow(getString(R.string.mode_flow), "Flow"),
    Tail(getString(R.string.mode_tail), "Tail"),
    Run(getString(R.string.mode_run), "Run"),
    RunBack(getString(R.string.mode_run_back), "RunBack"),
    Other(getString(R.string.mode_other), "Other"),
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
