package com.ledvance.nfc.converter.mapping

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/2/26 09:38
 * Describe : Mapping
 */
internal sealed class Mapping(val version: Int) {
    data object Charger : Mapping(1)
    data object Unknown : Mapping(-1)

    companion object {
        fun valueOf(version: Int): Mapping {
            return when (version) {
                Charger.version -> Charger
                else -> Unknown
            }
        }
    }
}