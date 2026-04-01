package com.ledvance.nfc.data.repository

import android.app.Activity
import android.content.Intent
import com.ledvance.nfc.data.model.NfcModel
import com.ledvance.nfc.utils.NfcProgressState
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/23 13:43
 * Describe : NfcDataRepository
 */
interface NfcDataRepository {
    fun dispatchActivityOnResume(activity: Activity, intent: Intent?)
    fun dispatchActivityOnPause(activity: Activity)
    fun hasSupportNfc(): Boolean
    val nfcEnable: StateFlow<Boolean>
    val nfcModel: StateFlow<NfcModel?>
    fun updateDriverModel(nfcModel: NfcModel?)

    val writingProgress: StateFlow<NfcProgressState>
    val readingProgress: StateFlow<NfcProgressState>

    fun enableNfcReader()
    fun enableNfcWriter()
    fun disableNfcReaderOrWriter()
    fun reset()
    fun refreshNfcData()
}