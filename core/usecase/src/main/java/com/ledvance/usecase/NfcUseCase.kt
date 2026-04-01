package com.ledvance.usecase

import android.app.Activity
import android.content.Intent
import com.ledvance.nfc.data.model.NfcModel
import com.ledvance.nfc.data.repository.NfcDataRepository
import com.ledvance.nfc.utils.NfcProgressState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/2/26 16:28
 * Describe : NfcUseCase
 */
@Singleton
class NfcUseCase @Inject constructor(
    private val nfcDataRepository: NfcDataRepository,
) : NfcDataRepository {
    override fun dispatchActivityOnResume(activity: Activity, intent: Intent?) {
        nfcDataRepository.dispatchActivityOnResume(activity, intent)
    }

    override fun dispatchActivityOnPause(activity: Activity) {
        nfcDataRepository.dispatchActivityOnPause(activity)
    }

    override fun hasSupportNfc() = nfcDataRepository.hasSupportNfc()

    override val nfcEnable: StateFlow<Boolean> = nfcDataRepository.nfcEnable
    override val nfcModel: StateFlow<NfcModel?> = nfcDataRepository.nfcModel

    override fun updateNfcModel(nfcModel: NfcModel?) {
        nfcDataRepository.updateNfcModel(nfcModel)
    }

    override val writingProgress: StateFlow<NfcProgressState> = nfcDataRepository.writingProgress
    override val readingProgress: StateFlow<NfcProgressState> = nfcDataRepository.readingProgress

    override fun enableNfcReader() {
        nfcDataRepository.enableNfcReader()
    }

    override fun enableNfcWriter() {
        nfcDataRepository.enableNfcWriter()
    }

    override fun disableNfcReaderOrWriter() {
        nfcDataRepository.disableNfcReaderOrWriter()
    }

    override fun reset() {
        nfcDataRepository.reset()
    }

    override fun refreshNfcData() {
        nfcDataRepository.refreshNfcData()
    }
}