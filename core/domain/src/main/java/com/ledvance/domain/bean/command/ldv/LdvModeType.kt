package com.ledvance.domain.bean.command.ldv

import com.ledvance.domain.bean.command.Command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/1/26
 * Describe : LdvModeType — LDV Bedside 模式类型 (对应协议 SetMode cmd=0x04 的数据字节)
 */
enum class LdvModeType(override val command: Byte) : Command {
    EyeProtection(0x07),   // 护眼模式
    Sleep(0x09),           // 睡眠模式
    Wakeup(0x0A),          // 唤醒模式
    AlwaysOn(0x0B),        // 常亮模式
    ;

    companion object {
        fun fromInt(value: Int): LdvModeType? {
            val targetByte = value.toByte()
            return entries.find { it.command == targetByte }
        }
    }
}
