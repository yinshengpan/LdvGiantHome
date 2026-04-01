package com.ledvance.domain.bean.command.giant

import com.ledvance.domain.bean.command.Command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 13:03
 * Describe : OnOffType
 */
enum class OnOffType(override val command: Byte) : Command {
    ALL(0x00), // 所有灯（彩灯、白灯、黄灯）
    RGB(0x01), // 彩灯
    W(0x02), // 白灯
    CT(0x03), // 黄灯
    RGBW(0x04), // 彩灯和白灯
    RGBCT(0x05), // 彩灯和黄灯
    WCT(0x06), // 黄灯和白灯
    ;
}