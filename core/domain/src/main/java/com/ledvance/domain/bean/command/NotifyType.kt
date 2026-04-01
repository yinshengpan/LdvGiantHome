package com.ledvance.domain.bean.command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 11:44
 * Describe : NotifyType
 */
enum class NotifyType(override val command: Byte) : Command {
    GiantResponse(0x02), // 设备应答帧标识 (协议帧 Byte2)
    LdvResponse(0x5B), // 设备应答帧标识 (协议帧 Byte2)
}