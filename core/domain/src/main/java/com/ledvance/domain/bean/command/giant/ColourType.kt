package com.ledvance.domain.bean.command.giant

import com.ledvance.domain.bean.command.Command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 13:06
 * Describe : ColourType
 */
enum class ColourType(override val command: Byte) : Command {
    RGB(0x01), // 彩灯
    W(0x02), // 白灯
    CT(0x03), // 黄灯
    WCT(0x04), // 黄灯和白灯
}