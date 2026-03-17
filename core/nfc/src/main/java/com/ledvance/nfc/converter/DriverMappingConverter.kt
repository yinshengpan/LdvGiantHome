package com.ledvance.nfc.converter

import com.ledvance.nfc.converter.mapping.Mapping
import com.ledvance.nfc.converter.parser.EVChargerParser
import com.ledvance.nfc.converter.position.CommonPosition
import com.ledvance.nfc.converter.position.Position
import com.ledvance.nfc.converter.serializer.EVChargerSerializer
import com.ledvance.nfc.converter.serializer.IDriverSerializer
import com.ledvance.nfc.data.model.Company
import com.ledvance.nfc.data.model.DriverInfo
import com.ledvance.nfc.data.model.DriverModel
import com.ledvance.utils.AppContext
import com.ledvance.utils.extensions.jsonAsOrNull
import com.ledvance.utils.extensions.subArray
import com.ledvance.utils.extensions.toHex
import com.ledvance.utils.extensions.toInt
import com.ledvance.utils.extensions.tryCatchReturn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/7 10:03
 * Describe : DriverMappingConverter
 */
internal object DriverMappingConverter {
    private const val TAG = "DriverMappingConverter"
    private var driverList: List<DriverInfo>? = null
    private val parserMap by lazy {
        mapOf(
            Mapping.Charger to EVChargerParser(),
        )
    }

    private val serializerMap by lazy {
        mapOf<Mapping, IDriverSerializer>(
            Mapping.Charger to EVChargerSerializer()
        )
    }

    suspend fun parseDriver(byteArray: ByteArray?): DriverModel? = withContext(Dispatchers.IO) {
        if (byteArray == null) {
            throw NullPointerException("parseDriver byteArray is null")
        }
        val driverInfo = parseDriverInfo(byteArray) ?: return@withContext null
        val mapping = Mapping.valueOf(driverInfo.mapping)
        tryCatchReturn { parserMap[mapping]?.parse(driverInfo, byteArray) }
    }

    suspend fun serializeDriver(model: DriverModel, array: ByteArray): Pair<ByteArray, Position>? =
        withContext(Dispatchers.IO) {
            val mapping = Mapping.valueOf(model.driverInfo.mapping)
            val serializer = serializerMap[mapping] ?: return@withContext null
            return@withContext tryCatchReturn { serializer.serialize(model, array) }
        }

    suspend fun parseDriverInfo(byteArray: ByteArray?): DriverInfo? = withContext(Dispatchers.IO) {
        val driverType = byteArray?.parseDriverType() ?: -1
        val company = byteArray?.parseCompany()
        if (company != Company.Ledvance.type) {
            Timber.tag(TAG).e("parseDriverInfo: company is not Ledvance")
            return@withContext null
        }
        val driverInfo = parseDriverInfo(predicate = {
            it.driverType == driverType
        })
        return@withContext driverInfo
    }

    private suspend fun ByteArray.parseDriverType(): Int = withContext(Dispatchers.IO) {
        val position = CommonPosition.DriverTypeCode
        val driverTypeHex = subArray(position.startIndex, position.endIndex)
        val driverType = driverTypeHex.toInt()
        Timber.tag(TAG).i("parseDriverType: ${driverTypeHex.toHex()} $driverType")
        return@withContext driverType
    }

    private suspend fun ByteArray.parseCompany(): Int = withContext(Dispatchers.IO) {
        val position = CommonPosition.CompanyCode
        val companyHex = subArray(position.startIndex, position.endIndex)
        val company = companyHex.toInt()
        Timber.tag(TAG).i("parseCompany: ${companyHex.toHex()} $company")
        return@withContext company
    }

    private suspend fun parseDriverInfo(predicate: (DriverInfo) -> Boolean): DriverInfo? {
        if (driverList == null) {
            driverList = loadDriverList()
        }
        return driverList?.find { predicate.invoke(it) }
    }

    private suspend fun loadDriverList() = withContext(Dispatchers.IO) {
        tryCatchReturn {
            AppContext.get().assets.open("driver_device.json").use {
                val size = it.available()
                val buffer = ByteArray(size)
                it.read(buffer)
                String(buffer, Charsets.UTF_8).jsonAsOrNull<List<DriverInfo>>()
            }
        } ?: listOf()
    }
}