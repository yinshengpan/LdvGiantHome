package com.ledvance.nfc.converter.serializer

import com.ledvance.nfc.converter.parser.feature.Feature
import com.ledvance.nfc.converter.parser.feature.IFeatureParser
import com.ledvance.nfc.converter.serializer.feature.IFeatureSerializer
import com.ledvance.nfc.data.model.Crc8Check
import com.ledvance.nfc.data.model.NfcInfo
import com.ledvance.nfc.data.model.NfcModel

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 14:49
 * Describe : FeatureConverter
 */
internal object FeatureConverter {
    private val featureParserMap by lazy {
        mapOf<Feature, IFeatureParser<*>>()
    }

    private val featureSerializerMap by lazy {
        mapOf<Feature, IFeatureSerializer>()
    }

    private suspend fun <T> parse(feature: Feature, byteArray: ByteArray): T? {
        return featureParserMap[feature]?.parse(byteArray) as? T
    }

    private fun ByteArray.serialize(feature: Feature, nfcModel: NfcModel): ByteArray {
        return featureSerializerMap[feature]?.serialize(nfcModel, this) ?: this
    }

    class FeatureSerializerChain(
        private var byteArray: ByteArray,
        private val model: NfcModel
    ) {
        fun apply(feature: Feature): FeatureSerializerChain {
            byteArray = byteArray.serialize(feature, model)
            return this
        }

        fun result(): ByteArray = byteArray
    }

    class FeatureParserChain(
        nfcInfo: NfcInfo,
        crc8Check: Crc8Check,
        private val byteArray: ByteArray,
    ) {
        var nfcModel = NfcModel(nfcInfo, crc8Check)
        suspend fun apply(feature: Feature): FeatureParserChain {
            nfcModel = when (feature) {
                else -> nfcModel
            }
            return this
        }

        fun result(): NfcModel = nfcModel
    }
}