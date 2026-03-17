package com.ledvance.nfc.converter.parser.feature

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 14:53
 * Describe : IFeatureParser
 */
internal interface IFeatureParser<T> {
    val TAG: String
        get() = this::class.java.simpleName

    suspend fun parse(byteArray: ByteArray): T?
}