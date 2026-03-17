package com.ledvance.utils.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/20 08:49
 * Describe : NumberExtensions
 */

fun Double.roundTo(decimals: Int = 1): Double {
    return BigDecimal(this)
        .setScale(decimals, RoundingMode.HALF_UP)
        .toDouble()
}


/** 转 Int（向下取整，可按需改） */
fun Number.toIntValue(): Int =
    when (this) {
        is Int -> this
        is Long -> this.toInt()
        else -> this.toDouble().roundToInt()
    }

/** 保留 1 位小数（去掉多余 0） */
fun Number.to1Decimal(): String =
    formatDecimal(1)

/** 保留 2 位小数（去掉多余 0） */
fun Number.to2Decimal(): String =
    formatDecimal(2)

/** 内部统一实现 */
private fun Number.formatDecimal(scale: Int): String =
    BigDecimal(this.toString())
        .setScale(scale, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()