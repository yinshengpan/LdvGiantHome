package com.ledvance.nfc.converter.serializer

import com.ledvance.nfc.converter.parser.feature.EVChargerDriverParmParser
import com.ledvance.nfc.converter.parser.feature.Feature
import com.ledvance.nfc.converter.serializer.feature.EVChargerDriverParamSerializer
import com.ledvance.nfc.data.model.Crc8Check
import com.ledvance.nfc.data.model.DriverInfo
import com.ledvance.nfc.data.model.DriverModel
import com.ledvance.nfc.data.model.EVChargerParam

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 14:49
 * Describe : FeatureConverter
 */
internal object FeatureConverter {
    private val featureParserMap by lazy {
        mapOf(
            Feature.EVChargerDriverParam to EVChargerDriverParmParser(),
        )
    }

    private val featureSerializerMap by lazy {
        mapOf(
            Feature.EVChargerDriverParam to EVChargerDriverParamSerializer(),
        )
    }

    private suspend fun <T> parse(feature: Feature, byteArray: ByteArray): T? {
        return featureParserMap[feature]?.parse(byteArray) as? T
    }

    private fun ByteArray.serialize(feature: Feature, driverModel: DriverModel): ByteArray {
        return featureSerializerMap[feature]?.serialize(driverModel, this) ?: this
    }

    class FeatureSerializerChain(
        private var byteArray: ByteArray,
        private val model: DriverModel
    ) {
        fun apply(feature: Feature): FeatureSerializerChain {
            byteArray = byteArray.serialize(feature, model)
            return this
        }

        fun result(): ByteArray = byteArray
    }

    class FeatureParserChain(
        driverInfo: DriverInfo,
        crc8Check: Crc8Check,
        private val byteArray: ByteArray,
    ) {
        var driverModel = DriverModel(driverInfo, crc8Check)
        suspend fun apply(feature: Feature): FeatureParserChain {
            driverModel = when (feature) {
                Feature.EVChargerDriverParam -> {
                    val evChargeDriverParam =
                        parse<EVChargerParam>(Feature.EVChargerDriverParam, byteArray)
                    driverModel.copy(evChargerParam = evChargeDriverParam)
                }

                else -> driverModel
            }
            return this
        }

        fun result(): DriverModel = driverModel
    }
}