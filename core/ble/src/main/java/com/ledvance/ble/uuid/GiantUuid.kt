package com.ledvance.ble.uuid

import java.util.UUID

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/1/26 10:47
 * Describe : GiantUuid
 */
internal class GiantUuid : IUuid {
    override fun getServiceUuid(): UUID {
        return UUID.fromString("0000FFF0-0000-1000-8000-00805F9B34FB")
    }

    override fun getWriteCharUuid(): UUID {
        return UUID.fromString("0000FFF3-0000-1000-8000-00805F9B34FB")
    }

    override fun getNotifyCharUuid(): UUID {
        return UUID.fromString("0000FFF4-0000-1000-8000-00805F9B34FB")
    }
}