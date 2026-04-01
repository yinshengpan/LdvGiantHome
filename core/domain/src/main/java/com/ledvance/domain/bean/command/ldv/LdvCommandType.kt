package com.ledvance.domain.bean.command.ldv

import com.ledvance.domain.bean.command.Command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/1/26
 * Describe : LdvCommandType — LDV Bedside 下行命令类型 (下行帧 [0x5A][cmd][data_len][data])
 *
 * 上报命令请参见 [LdvReportType]
 */
enum class LdvCommandType(override val command: Byte) : Command {
    SetPower(0x01),         // 开关: data=0x01(开)/0x00(关)
    SetBrightness(0x02),    // 亮度: data=0x01~0x32 (1~50)
    SetCct(0x03),           // 色温: data=[色温L, 色温H] (1800K~6500K)
    SetMode(0x04),          // 模式: data=[模式字节] 参见 LdvModeType
    QueryDeviceInfo(0x05),  // 获取灯状态: data=0x01
    TimeSync(0x06),         // 时间同步
    SetTimer(0x07),         // 添加/修改定时任务
    QueryTimer(0x08),       // 查询定时状态: data=0x01
    DeleteTimer(0x09),      // 删除定时任务: data=[定时器ID]
    ;
}
