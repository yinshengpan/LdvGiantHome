package com.ledvance.nfc.data.repository

import android.app.Activity
import android.content.Intent
import com.ledvance.nfc.LDVNfcManager
import com.ledvance.nfc.data.model.NfcModel
import com.ledvance.nfc.utils.NfcProgressState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/23 13:45
 * Describe : NfcDataRepositoryImpl
 */
class NfcDataRepositoryImpl @Inject constructor() : NfcDataRepository {
    override fun dispatchActivityOnResume(
        activity: Activity,
        intent: Intent?
    ) {
        LDVNfcManager.dispatchActivityOnResume(activity, intent)
    }

    override fun dispatchActivityOnPause(activity: Activity) {
        LDVNfcManager.dispatchActivityOnPause(activity)
    }

    override fun hasSupportNfc(): Boolean {
        return LDVNfcManager.hasSupportNfc()
    }

    override val nfcEnable: StateFlow<Boolean>
        get() = LDVNfcManager.nfcEnable
    override val nfcModel: StateFlow<NfcModel?>
        get() = LDVNfcManager.nfcModel

    override fun updateDriverModel(nfcModel: NfcModel?) {
        LDVNfcManager.updateDriverModel(nfcModel)
    }

    override val writingProgress: StateFlow<NfcProgressState>
        get() = LDVNfcManager.writingProgress
    override val readingProgress: StateFlow<NfcProgressState>
        get() = LDVNfcManager.readingProgress

    override fun enableNfcReader() {
        LDVNfcManager.enableNfcReader()
    }

    override fun enableNfcWriter() {
        LDVNfcManager.enableNfcWriter()
    }

    override fun disableNfcReaderOrWriter() {
        LDVNfcManager.disableNfcReaderOrWriter()
    }

    override fun reset() {
        LDVNfcManager.reset()
    }

    override fun refreshNfcData() {
        LDVNfcManager.refreshNfcData()
    }
}