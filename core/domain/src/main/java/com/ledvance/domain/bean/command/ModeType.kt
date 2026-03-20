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
}
