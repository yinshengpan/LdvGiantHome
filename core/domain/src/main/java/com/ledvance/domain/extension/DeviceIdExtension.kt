package com.ledvance.domain.extension

import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/1/26 11:18
 * Describe : DeviceIdExtension
 */
fun DeviceId.isGiantDevice(): Boolean {
    return deviceType == DeviceType.GiantTable || deviceType == DeviceType.GiantFloor
}