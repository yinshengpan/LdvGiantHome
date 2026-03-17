package com.ledvance.nfc.utils


/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/6 17:57
 * Describe : Unit
 */
sealed class LDVUnit(val name: String) {
    data object Percentage : LDVUnit("%")
    data object Seconds : LDVUnit("S")
    data object TenSecondsMultiple : LDVUnit("*10 S")
    data object MilliAmpere : LDVUnit("mA")
    data object Power : LDVUnit("W")
    data object Voltage : LDVUnit("V")
}