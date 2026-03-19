package com.ledvance.domain.bean.command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/19 20:25
 * Describe : MagicColorMode
 */
data class MagicColorMode(val id: Int, val name: String) {
    companion object {
        val BaseModes = listOf(
            MagicColorMode(1, "自动循环"),
            MagicColorMode(2, "正向幻彩"),
            MagicColorMode(3, "反向幻彩"),
            MagicColorMode(4, "七彩能量"),
            MagicColorMode(5, "七彩跳变"),
            MagicColorMode(6, "红绿蓝跳变"),
            MagicColorMode(7, "七彩呼吸"),
            MagicColorMode(8, "红绿蓝呼吸"),
            MagicColorMode(9, "七彩渐变"),
            MagicColorMode(10, "三色渐变"),
        )

        val CurtainModes = listOf(
            MagicColorMode(43, "七彩刷色拉幕"),
            MagicColorMode(44, "红绿蓝刷色闭幕"),
            MagicColorMode(45, "红绿蓝刷色拉幕"),
            MagicColorMode(46, "黄青紫刷色闭幕"),
            MagicColorMode(47, "黄青紫刷色拉幕"),
            MagicColorMode(48, "红绿蓝刷色循环"),
            MagicColorMode(49, "七彩刷色循环"),
            MagicColorMode(50, "红紫刷色闭幕"),
            MagicColorMode(51, "红紫刷色拉幕"),
            MagicColorMode(52, "绿蓝刷色循环"),
        )

        val TransitionModes = listOf(
            MagicColorMode(20, "七彩流光"),
            MagicColorMode(21, "彩虹流动"),
            MagicColorMode(22, "极光闪烁"),
            MagicColorMode(23, "流星划过"),
            MagicColorMode(24, "色彩交替"),
            MagicColorMode(25, "波浪翻滚"),
            MagicColorMode(26, "雷电闪烁"),
            MagicColorMode(27, "烟花盛放"),
            MagicColorMode(28, "气泡上升"),
            MagicColorMode(29, "雪花飘零"),
        )
        
        fun getModesBySegment(segment: String): List<MagicColorMode> {
            return when (segment) {
                "Base" -> BaseModes
                "Curtain" -> CurtainModes
                "Transition" -> TransitionModes
                else -> BaseModes
            }
        }
    }
}
