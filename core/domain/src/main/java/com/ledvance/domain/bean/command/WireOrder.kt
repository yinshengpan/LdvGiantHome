package com.ledvance.domain.bean.command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 14:25
 * Describe : WireOrder
 */
enum class WireOrder(override val command: Byte) : Command {
    RGB(0x01),   // 红-绿-蓝
    RBG(0x02),   // 红-蓝-绿
    BRG(0x03),   // 蓝-红-绿
    BGR(0x04),   // 蓝-绿-红
    GRB(0x05),   // 绿-红-蓝
    GBR(0x06),   // 绿-蓝-红
    ;
}