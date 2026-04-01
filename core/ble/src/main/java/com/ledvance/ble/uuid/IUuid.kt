package com.ledvance.ble.uuid

import com.ledvance.domain.bean.DeviceType
import java.util.UUID

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/1/26 10:47
 * Describe : IUuid
 */
internal interface IUuid {
    fun getServiceUuid(): UUID
    fun getWriteCharUuid(): UUID
    fun getNotifyCharUuid(): UUID
}

internal fun DeviceType.getUuidData(): IUuid {
    return when (this) {
        DeviceType.GiantTable, DeviceType.GiantFloor -> GiantUuid()
        DeviceType.LdvBedside -> LdvBedsideUuid()
    }
}