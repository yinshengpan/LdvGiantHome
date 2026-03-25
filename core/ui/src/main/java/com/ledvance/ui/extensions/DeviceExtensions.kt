package com.ledvance.ui.extensions

import com.ledvance.domain.bean.DeviceType
import com.ledvance.ui.R

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:18
 * Describe : DeviceExtensions
 */
fun DeviceType.getIconResId(): Int {
    return when (this) {
        DeviceType.GiantTable -> R.mipmap.pic_tablelamp
        DeviceType.GiantFloor -> R.mipmap.pic_floorlamp
    }
}