package com.ledvance.nfc.data.model

import com.st.st25sdk.NFCTag
import com.st.st25sdk.TagHelper.ProductID

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 19:09
 * Describe : TagInfo
 */
internal data class TagInfo(val nfcTag: NFCTag?, val productID: ProductID)