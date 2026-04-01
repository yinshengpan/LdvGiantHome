package com.ledvance.nfc.utils

import android.nfc.Tag
import com.ledvance.nfc.data.model.TagInfo
import com.ledvance.nfc.exception.CannotGetAreaIdException
import com.ledvance.nfc.exception.TypeNotSupportException
import com.ledvance.utils.extensions.toHexLn
import com.ledvance.utils.extensions.tryCatchReturn
import com.st.st25android.AndroidReaderInterface
import com.st.st25sdk.Helper
import com.st.st25sdk.NFCTag
import com.st.st25sdk.STException
import com.st.st25sdk.TagHelper
import com.st.st25sdk.iso14443b.Iso14443bTag
import com.st.st25sdk.type2.Type2Tag
import com.st.st25sdk.type2.st25tn.ST25TNTag
import com.st.st25sdk.type4a.Type4Tag
import com.st.st25sdk.type4a.m24srtahighdensity.M24SR02KTag
import com.st.st25sdk.type4a.m24srtahighdensity.M24SR04KTag
import com.st.st25sdk.type4a.m24srtahighdensity.M24SR16KTag
import com.st.st25sdk.type4a.m24srtahighdensity.M24SR64KTag
import com.st.st25sdk.type4a.m24srtahighdensity.ST25TA16KTag
import com.st.st25sdk.type4a.m24srtahighdensity.ST25TA64KTag
import com.st.st25sdk.type4a.st25ta.ST25TA02KBDTag
import com.st.st25sdk.type4a.st25ta.ST25TA02KBPTag
import com.st.st25sdk.type4a.st25ta.ST25TA02KBTag
import com.st.st25sdk.type4a.st25ta.ST25TA02KDTag
import com.st.st25sdk.type4a.st25ta.ST25TA02KPTag
import com.st.st25sdk.type4a.st25ta.ST25TA02KTag
import com.st.st25sdk.type4a.st25ta.ST25TA512BTag
import com.st.st25sdk.type4a.st25ta.ST25TA512Tag
import com.st.st25sdk.type4b.Type4bTag
import com.st.st25sdk.type5.STType5Tag
import com.st.st25sdk.type5.Type5Tag
import com.st.st25sdk.type5.lri.LRi1KTag
import com.st.st25sdk.type5.lri.LRi2KTag
import com.st.st25sdk.type5.lri.LRi512Tag
import com.st.st25sdk.type5.lri.LRiS2KTag
import com.st.st25sdk.type5.m24lr.LRiS64KTag
import com.st.st25sdk.type5.m24lr.M24LR04KTag
import com.st.st25sdk.type5.m24lr.M24LR16KTag
import com.st.st25sdk.type5.m24lr.M24LR64KTag
import com.st.st25sdk.type5.st25dv.ST25DVCTag
import com.st.st25sdk.type5.st25dv.ST25DVTag
import com.st.st25sdk.type5.st25dv.ST25TV04KPTag
import com.st.st25sdk.type5.st25dv.ST25TV16KTag
import com.st.st25sdk.type5.st25dv.ST25TV64KTag
import com.st.st25sdk.type5.st25dvpwm.ST25DV02KW1Tag
import com.st.st25sdk.type5.st25dvpwm.ST25DV02KW2Tag
import com.st.st25sdk.type5.st25tv.ST25TVTag
import com.st.st25sdk.type5.st25tvc.ST25TVCTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 19:08
 * Describe : NfcTagUtils
 */
internal object NfcTagUtils {
    private const val TAG = "NfcTagUtils"

    private const val DEFAULT_DATA_SIZE = 128 * 4

    suspend fun parseNfcByteArray(
        nfcTag: NFCTag,
        startAddress: Int = 0,
        numberOfBytes: Int = DEFAULT_DATA_SIZE
    ): ByteArray? = withContext(Dispatchers.IO) {
        Timber.tag(TAG).d("parseNfcByteArray: nfcTag=$nfcTag")
        return@withContext tryCatchReturn {
            when (nfcTag) {
                is Type5Tag -> {
                    throw TypeNotSupportException("Type5Tag")
                }

                is Type4Tag -> {
                    throw TypeNotSupportException("Type4Tag")
                }

                is Type2Tag -> {
                    val areaId = nfcTag.getAreaIdFromAddressInBytesForType2Tag(startAddress)
                    Timber.tag(TAG).i("parseNfcByteArray: areaId=$areaId")
                    if (areaId == -1) throw CannotGetAreaIdException()
                    val result: ByteArray = nfcTag.readBytes(startAddress, numberOfBytes)
                    Timber.tag(TAG).i("parseNfcByteArray reading nfcTag -> ${result.toHexLn()}")
                    result
                }

                else -> {
                    throw TypeNotSupportException("UnknownTag")
                }
            }
        }
    }

    fun parseTagInfo(androidTag: Tag): TagInfo {
        Timber.tag(TAG).d("parseTagInfo: androidTag=$androidTag")
        val readerInterface = tryCatchReturn { AndroidReaderInterface.newInstance(androidTag) }
            ?: return TagInfo(null, TagHelper.ProductID.PRODUCT_UNKNOWN)
        Timber.tag(TAG).d("parseTagInfo: readerInterface=$readerInterface")
        var nfcTag: NFCTag? = null
        var uid = androidTag.id
        var productID = tryCatchReturn {
            when (readerInterface.mTagType) {
                NFCTag.NfcTagTypes.NFC_TAG_TYPE_V -> {
                    uid = Helper.reverseByteArray(uid)
                    TagHelper.identifyTypeVProduct(readerInterface, uid)
                }

                NFCTag.NfcTagTypes.NFC_TAG_TYPE_4A ->
                    TagHelper.identifyType4Product(readerInterface, uid)

                NFCTag.NfcTagTypes.NFC_TAG_TYPE_2 ->
                    TagHelper.identifyIso14443aType2Type4aProduct(readerInterface, uid)

                NFCTag.NfcTagTypes.NFC_TAG_TYPE_4B ->
                    TagHelper.identifyIso14443BProduct(readerInterface, uid)

                NFCTag.NfcTagTypes.NFC_TAG_TYPE_A, NFCTag.NfcTagTypes.NFC_TAG_TYPE_B ->
                    TagHelper.ProductID.PRODUCT_UNKNOWN

                else -> TagHelper.ProductID.PRODUCT_UNKNOWN
            }
        } ?: TagHelper.ProductID.PRODUCT_UNKNOWN

        // Take advantage that we are in a background thread to allocate the NFCTag.
        try {
            when (productID) {
                TagHelper.ProductID.PRODUCT_ST_ST25DV64K_I, TagHelper.ProductID.PRODUCT_ST_ST25DV64K_J, TagHelper.ProductID.PRODUCT_ST_ST25DV16K_I, TagHelper.ProductID.PRODUCT_ST_ST25DV16K_J, TagHelper.ProductID.PRODUCT_ST_ST25DV04K_I, TagHelper.ProductID.PRODUCT_ST_ST25DV04K_J -> nfcTag =
                    ST25DVTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25DV04KC_I, TagHelper.ProductID.PRODUCT_ST_ST25DV04KC_J, TagHelper.ProductID.PRODUCT_ST_ST25DV16KC_I, TagHelper.ProductID.PRODUCT_ST_ST25DV16KC_J, TagHelper.ProductID.PRODUCT_ST_ST25DV64KC_I, TagHelper.ProductID.PRODUCT_ST_ST25DV64KC_J -> nfcTag =
                    ST25DVCTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_LRi512 -> nfcTag = LRi512Tag(readerInterface, uid)
                TagHelper.ProductID.PRODUCT_ST_LRi1K -> nfcTag = LRi1KTag(readerInterface, uid)
                TagHelper.ProductID.PRODUCT_ST_LRi2K -> nfcTag = LRi2KTag(readerInterface, uid)
                TagHelper.ProductID.PRODUCT_ST_LRiS2K -> nfcTag = LRiS2KTag(readerInterface, uid)
                TagHelper.ProductID.PRODUCT_ST_LRiS64K -> nfcTag = LRiS64KTag(readerInterface, uid)
                TagHelper.ProductID.PRODUCT_ST_M24SR02_Y -> nfcTag =
                    M24SR02KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_M24SR04_Y, TagHelper.ProductID.PRODUCT_ST_M24SR04_G -> nfcTag =
                    M24SR04KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_M24SR16_Y -> nfcTag =
                    M24SR16KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_M24SR64_Y -> nfcTag =
                    M24SR64KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TV512, TagHelper.ProductID.PRODUCT_ST_ST25TV02K -> nfcTag =
                    ST25TVTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TV04K_P -> nfcTag =
                    ST25TV04KPTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TV02KC, TagHelper.ProductID.PRODUCT_ST_ST25TV512C -> nfcTag =
                    ST25TVCTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TV16K -> nfcTag =
                    ST25TV16KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TV64K -> nfcTag =
                    ST25TV64KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25DV02K_W1 -> nfcTag =
                    ST25DV02KW1Tag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25DV02K_W2 -> nfcTag =
                    ST25DV02KW2Tag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_M24LR16E_R -> nfcTag =
                    M24LR16KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_M24LR64E_R, TagHelper.ProductID.PRODUCT_ST_M24LR64_R -> nfcTag =
                    M24LR64KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_M24LR04E_R -> nfcTag =
                    M24LR04KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA02K -> nfcTag =
                    ST25TA02KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA02KB -> nfcTag =
                    ST25TA02KBTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA02K_P -> nfcTag =
                    ST25TA02KPTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA02KB_P -> nfcTag =
                    ST25TA02KBPTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA02K_D -> nfcTag =
                    ST25TA02KDTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA02KB_D -> nfcTag =
                    ST25TA02KBDTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA16K -> nfcTag =
                    ST25TA16KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA512_K, TagHelper.ProductID.PRODUCT_ST_ST25TA512 -> nfcTag =
                    ST25TA512Tag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA512B -> nfcTag =
                    ST25TA512BTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_ST_ST25TA64K -> nfcTag =
                    ST25TA64KTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_GENERIC_TYPE4, TagHelper.ProductID.PRODUCT_GENERIC_TYPE4A -> nfcTag =
                    Type4Tag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_GENERIC_TYPE4B -> nfcTag =
                    Type4bTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_GENERIC_ISO14443B -> nfcTag =
                    Iso14443bTag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_GENERIC_TYPE5_AND_ISO15693 -> nfcTag =
                    STType5Tag(readerInterface, uid)

                TagHelper.ProductID.PRODUCT_GENERIC_TYPE5 -> nfcTag = Type5Tag(readerInterface, uid)
                TagHelper.ProductID.PRODUCT_GENERIC_TYPE2 -> nfcTag = Type2Tag(readerInterface, uid)
                TagHelper.ProductID.PRODUCT_ST_ST25TN01K, TagHelper.ProductID.PRODUCT_ST_ST25TN512 -> nfcTag =
                    ST25TNTag(readerInterface, uid)

                else -> {
                    nfcTag = null
                    productID = TagHelper.ProductID.PRODUCT_UNKNOWN
                }
            }
        } catch (e: STException) {
            // An STException has occured while instantiating the tag
            Timber.tag(TAG).e(e, "parseTagInfo: ")
            productID = TagHelper.ProductID.PRODUCT_UNKNOWN
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "parseTagInfo: ")
        }
        if (nfcTag != null) {
            var manufacturerName = ""
            try {
                manufacturerName = nfcTag.manufacturerName
            } catch (e: STException) {
                e.printStackTrace()
            }
            if (manufacturerName == "STMicroelectronics") {
                nfcTag.name = productID.toString()
            }
        }
        return TagInfo(nfcTag, productID)
    }
}