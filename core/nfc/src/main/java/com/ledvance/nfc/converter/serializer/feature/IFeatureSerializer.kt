package com.ledvance.nfc.converter.serializer.feature

import com.ledvance.nfc.data.model.DriverModel

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 14:43
 * Describe : IFeatureSerializer
 */
internal interface IFeatureSerializer {
    val TAG
        get() = this::class.java.simpleName
    fun serialize(driverModel: DriverModel, byteArray: ByteArray): ByteArray
}