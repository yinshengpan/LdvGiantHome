package com.ledvance.ble.uuid

import java.util.UUID

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/1/26 10:47
 * Describe : LdvBedsideUuid
 */
internal class LdvBedsideUuid : IUuid {
    override fun getServiceUuid(): UUID {
        return UUID.fromString("0000F1F0-0000-1000-8000-00805F9B34FB")
    }

    override fun getWriteCharUuid(): UUID {
        return UUID.fromString("0000F1F1-0000-1000-8000-00805F9B34FB")
    }

    override fun getNotifyCharUuid(): UUID {
        return UUID.fromString("0000F1F2-0000-1000-8000-00805F9B34FB")
    }
}