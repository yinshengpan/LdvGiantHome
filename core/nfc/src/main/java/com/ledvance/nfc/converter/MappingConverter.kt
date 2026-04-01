package com.ledvance.nfc.converter

import com.ledvance.domain.bean.Company
import com.ledvance.domain.bean.DeviceType
import com.ledvance.nfc.converter.mapping.Mapping
import com.ledvance.nfc.converter.parser.BedsideLampParser
import com.ledvance.nfc.converter.position.CommonPosition
import com.ledvance.nfc.converter.position.Position
import com.ledvance.nfc.converter.serializer.BedsideLampSerializer
import com.ledvance.nfc.converter.serializer.INfcSerializer
import com.ledvance.nfc.data.model.NfcInfo
import com.ledvance.nfc.data.model.NfcModel
import com.ledvance.utils.extensions.subArray
import com.ledvance.utils.extensions.toHex
import com.ledvance.utils.extensions.toInt
import com.ledvance.utils.extensions.toMacAddress
import com.ledvance.utils.extensions.tryCatchReturn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/7 10:03
 * Describe : MappingConverter
 */
internal object MappingConverter {
    private const val TAG = "MappingConverter"
    private val parserMap by lazy {
        mapOf(
            Mapping.BedsideLamp to BedsideLampParser(),
        )
    }

    private val serializerMap by lazy {
        mapOf<Mapping, INfcSerializer>(
            Mapping.BedsideLamp to BedsideLampSerializer()
        )
    }

    suspend fun parse(byteArray: ByteArray?): NfcModel? = withContext(Dispatchers.IO) {
        if (byteArray == null) {
            throw NullPointerException("parse byteArray is null")
        }
        val nfcInfo = parseNfcInfo(byteArray)
        tryCatchReturn { parserMap[nfcInfo.mapping]?.parse(nfcInfo, byteArray) }
    }

    suspend fun serialize(model: NfcModel, array: ByteArray): Pair<ByteArray, Position>? =
        withContext(Dispatchers.IO) {
            val serializer = serializerMap[model.nfcInfo.mapping] ?: return@withContext null
            return@withContext tryCatchReturn { serializer.serialize(model, array) }
        }

    suspend fun parseNfcInfo(byteArray: ByteArray?): NfcInfo = withContext(Dispatchers.IO) {
        val type = byteArray?.parseDeviceType() ?: -1
        val company = byteArray?.parseCompany() ?: -1
        val macAddress = byteArray?.parseMacAddress() ?: ""
        Timber.tag(TAG).e("parseNfcInfo: driverType:$type, company:$company, macAddress:$macAddress")
        val deviceType = when (type) {
            1 -> DeviceType.LdvBedside
            else -> DeviceType.LdvBedside
        }
        return@withContext NfcInfo(
            macAddress = macAddress,
            deviceType = deviceType,
            mapping = Mapping.deviceTypeOf(deviceType),
            company = Company.typeOf(company),
        )
    }

    private suspend fun ByteArray.parseDeviceType(): Int = withContext(Dispatchers.IO) {
        val position = CommonPosition.DeviceTypeCode
        val deviceTypeHex = subArray(position.startIndex, position.endIndex)
        val deviceType = deviceTypeHex.toInt()
        Timber.tag(TAG).i("parseDeviceType: ${deviceTypeHex.toHex()} $deviceType")
        return@withContext deviceType
    }

    private suspend fun ByteArray.parseCompany(): Int = withContext(Dispatchers.IO) {
        val position = CommonPosition.CompanyCode
        val companyHex = subArray(position.startIndex, position.endIndex)
        val company = companyHex.toInt()
        Timber.tag(TAG).i("parseCompany: ${companyHex.toHex()} $company")
        return@withContext company
    }

    private suspend fun ByteArray.parseMacAddress(): String = withContext(Dispatchers.IO) {
        val position = CommonPosition.DeviceMacAddress
        val macAddressHex: ByteArray = subArray(position.startIndex, position.endIndex)
        val macAddress = macAddressHex.toMacAddress()
        Timber.tag(TAG).i("parseMacAddress: ${macAddressHex.toHex()} $macAddress")
        return@withContext macAddress
    }
}