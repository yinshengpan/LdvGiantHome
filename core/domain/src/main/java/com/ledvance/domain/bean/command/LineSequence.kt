package com.ledvance.domain.bean.command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 14:25
 * Describe : LineSequence
 */
enum class LineSequence(val title: String, override val command: Byte) : Command {
    RGB("RGB", 0x01),   // 红-绿-蓝
    RBG("RBG", 0x02),   // 红-蓝-绿
    BRG("BRG", 0x03),   // 蓝-红-绿
    BGR("BGR", 0x04),   // 蓝-绿-红
    GRB("GRB", 0x05),   // 绿-红-蓝
    GBR("GBR", 0x06),   // 绿-蓝-红
    ;

    companion object {

        val items = LineSequence.entries.toList()

        fun fromInt(value: Int): LineSequence? {
            // 将 Int 转为 Byte（自动截断高位，符合 Byte 取值范围 -128~127）
            val targetByte = value.toByte()
            // 遍历所有枚举项，匹配 command 字节值
            return LineSequence.entries.find { it.command == targetByte }
        }

    }
}