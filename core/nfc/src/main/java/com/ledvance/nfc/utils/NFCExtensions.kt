package com.ledvance.nfc.utils

import com.st.st25sdk.MultiAreaInterface
import com.st.st25sdk.NFCTag
import com.st.st25sdk.STException
import com.st.st25sdk.type5.STType5Tag

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 19:31
 * Describe : NFCExtensions
 */
internal fun NFCTag.getAreaIdFromAddressInBytesForType5Tag(address: Int): Int {
    var ret = MultiAreaInterface.AREA1
    if (this is MultiAreaInterface && this is STType5Tag) {
        val tag = this as MultiAreaInterface
        ret = try {
            tag.getAreaFromByteAddress(address)
        } catch (e: STException) {
            -1
        }
    }
    return ret
}