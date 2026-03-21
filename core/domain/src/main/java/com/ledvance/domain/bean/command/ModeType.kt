package com.ledvance.domain.bean.command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26
 * Describe : ModeType - 模式类型 (对应协议 SetModeOrScene Byte2)
 */
enum class ModeType(override val command: Byte) : Command {
    Classic(0x01), // 经典模式，modeId 范围 0-212
    Scene(0x02),   // 场景模式，modeId 范围 1-33
    ;

    companion object {
        fun fromInt(value: Int): ModeType? {
            // 将 Int 转为 Byte（自动截断高位，符合 Byte 取值范围 -128~127）
            val targetByte = value.toByte()
            // 遍历所有枚举项，匹配 command 字节值
            return ModeType.entries.find { it.command == targetByte }
        }

    }
}
