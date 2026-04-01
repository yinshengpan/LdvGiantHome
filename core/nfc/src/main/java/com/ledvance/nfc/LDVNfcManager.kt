package com.ledvance.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import androidx.core.content.IntentCompat
import com.ledvance.nfc.converter.MappingConverter
import com.ledvance.nfc.data.model.NfcMode
import com.ledvance.nfc.data.model.NfcModel
import com.ledvance.nfc.utils.NfcProgressState
import com.ledvance.nfc.utils.NfcTagUtils
import com.ledvance.nfc.utils.getA1A2BodyRange
import com.ledvance.nfc.utils.getA2ContentRange
import com.ledvance.nfc.utils.subArray
import com.ledvance.utils.AppContext
import com.ledvance.utils.extensions.toHexLn
import com.ledvance.utils.extensions.tryCatch
import com.ledvance.utils.extensions.tryCatchReturn
import com.st.st25sdk.NFCTag
import com.st.st25sdk.STException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 19:24
 * Describe : LDVNfcManager
 */
internal object LDVNfcManager {
    private const val TAG = "LDVNfcManager"

    @Volatile
    private var currentNfcByteArray: ByteArray? = null
    private const val IS_PRINT_A1_A2_BODY: Boolean = true
    private val nfcModeFlow = MutableStateFlow<NfcMode>(NfcMode.Reader)
    private val nfcMode: StateFlow<NfcMode> = nfcModeFlow

    private val nfcAdapter: NfcAdapter? by lazy {
        tryCatchReturn { NfcAdapter.getDefaultAdapter(AppContext.get()) }
    }

    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO) }
    private val readingProgressFlow = MutableStateFlow(NfcProgressState.Idle)
    val readingProgress: StateFlow<NfcProgressState> = readingProgressFlow
    private val writingProgressFlow = MutableStateFlow(NfcProgressState.Idle)
    val writingProgress: StateFlow<NfcProgressState> = writingProgressFlow
    private val nfcModelFlow = MutableStateFlow<NfcModel?>(null)
    val nfcModel: StateFlow<NfcModel?> = nfcModelFlow
    val nfcEnableFlow = MutableStateFlow(nfcAdapter?.isEnabled ?: false)
    val nfcEnable: StateFlow<Boolean> = nfcEnableFlow

    fun dispatchActivityOnResume(activity: Activity, intent: Intent?) {
        tryCatch {
            Timber.tag(TAG).i("enableForegroundDispatch")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
                && nfcAdapter?.isReaderOptionSupported == true
            ) {
                val isReaderOptionEnabled = nfcAdapter?.isReaderOptionEnabled
                Timber.tag(TAG)
                    .i("enableForegroundDispatch isReaderOptionEnabled:$isReaderOptionEnabled")
                nfcEnableFlow.update { nfcAdapter?.isEnabled == true && isReaderOptionEnabled == true }
            }
            enableForegroundDispatch(activity)
            val nfcTag = intent?.let {
                IntentCompat.getParcelableExtra(it, NfcAdapter.EXTRA_TAG, Tag::class.java)
            } ?: return
            parseNfcTag(nfcTag)
        }
    }

    fun dispatchActivityOnPause(activity: Activity) {
        tryCatch {
            Timber.tag(TAG).i("disableForegroundDispatch")
            nfcAdapter?.disableForegroundDispatch(activity)
        }
    }

    fun updateDriverModel(nfcModel: NfcModel?) {
        Timber.tag(TAG).i("updateDriverModel driverModel->$nfcModel")
        coroutineScope.launch {
            nfcModelFlow.update { nfcModel }
        }
    }

    private fun parseNfcTag(tag: Tag) {
        val nfcMode = nfcMode.value
        Timber.tag(TAG).i("parseTag: nfcMode->$nfcMode")
        when (nfcMode) {
            NfcMode.Disable -> {}
            NfcMode.Reader -> {
                disableNfcReaderOrWriter()
                readNfcTag(tag)
            }

            NfcMode.Writer -> {
                disableNfcReaderOrWriter()
                writeNfcTag(tag)
            }
        }
    }

    private fun readNfcTag(tag: Tag) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                readingProgressFlow.update { NfcProgressState.Programing }
                Timber.tag(TAG).d("readNfcTag: readingProgress->Programing")
                val nfcByteArray = tag.getNfcTag()?.let { NfcTagUtils.parseNfcByteArray(it) }
                Timber.tag(TAG).i("readNfcTag: nfcByteArray ->${nfcByteArray?.size} ")
                delay(300)
                readingProgressFlow.update { NfcProgressState.Verify }
                Timber.tag(TAG).d("readNfcTag: readingProgress->Verify")
                val driverModel = MappingConverter.parse(nfcByteArray)
                Timber.tag(TAG).i("readNfcTag: driverModel->$driverModel")
                if (driverModel == null) {
                    readingProgressFlow.update { NfcProgressState.Fail }
                    Timber.tag(TAG).e("readNfcTag: readingProgress->Fail")
                    return@launch
                }
                delay(300)
                if (IS_PRINT_A1_A2_BODY) {
                    val dataA1A2 = nfcByteArray?.subArray(driverModel.nfcInfo.getA1A2BodyRange())
                    Timber.tag(TAG).w("readNfcTag: dataA1A2 ${dataA1A2?.toHexLn()}")
                }
                currentNfcByteArray = nfcByteArray
                nfcModelFlow.update { driverModel }
                readingProgressFlow.update { NfcProgressState.Success }
                Timber.tag(TAG).d("readNfcTag: readingProgress->Success")
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "readNfcTag: ")
                readingProgressFlow.update { NfcProgressState.Fail }
                Timber.tag(TAG).d("readNfcTag: readingProgress->Error")
            }
        }
    }

    private fun writeNfcTag(tag: Tag) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                Timber.tag(TAG).d("writeNfcTag: writingProgress->Programing")
                writingProgressFlow.update { NfcProgressState.Programing }
                val nfcTag = tag.getNfcTag()
                val nfcByteArray = nfcTag?.let { NfcTagUtils.parseNfcByteArray(it) }
                val driverInfo = MappingConverter.parseNfcInfo(nfcByteArray)
                val model = nfcModelFlow.value
                val byteArray = currentNfcByteArray
                Timber.tag(TAG)
                    .i("writeNfcTag: new driver->${driverInfo?.deviceType} mapping->${driverInfo?.mapping}")
                Timber.tag(TAG)
                    .i("writeNfcTag: old driver->${model?.nfcInfo?.deviceType} mapping->${model?.nfcInfo?.mapping}")
                if (nfcTag == null || model == null || byteArray == null || driverInfo?.deviceType != model.nfcInfo.deviceType) {
                    writingProgressFlow.update { NfcProgressState.Fail }
                    Timber.tag(TAG).e("writeNfcTag: writingProgress->Fail")
                    return@launch
                }
                val position = model.nfcInfo.getA2ContentRange()
                val oldArrayData = byteArray.subArray(position)
                Timber.tag(TAG).i("writeNfcTag: oldArrayData:${oldArrayData.toHexLn()}")
                val (dataArray, dataPosition) = MappingConverter.serialize(
                    model = model,
                    array = byteArray
                ) ?: return@launch let {
                    writingProgressFlow.update { NfcProgressState.Fail }
                    Timber.tag(TAG).e("writeNfcTag: serializeDriver failed")
                }
                if (IS_PRINT_A1_A2_BODY) {
                    val dataA1A2 = dataArray.subArray(model.nfcInfo.getA1A2BodyRange())
                    Timber.tag(TAG).w("writeNfcTag: dataA1A2 ${dataA1A2.toHexLn()}")
                }
                val newArrayData = dataArray.subArray(position)
                Timber.tag(TAG)
                    .i("writeNfcTag: position:$dataPosition newArrayData:${newArrayData.toHexLn()}")
                var retryCount = 3
                suspend fun innerWriteNfcBytes() {
                    try {
                        retryCount--
                        if (retryCount <= 0) {
                            Timber.tag(TAG).e("writeNfcTag: retryCount:$retryCount")
                            writingProgressFlow.update { NfcProgressState.Fail }
                            return
                        }
                        writingProgressFlow.update { NfcProgressState.Verify }
                        nfcTag.writeBytes(dataPosition.startIndex, newArrayData)
                        writingProgressFlow.update { NfcProgressState.Success }
                        currentNfcByteArray = dataArray
                        Timber.tag(TAG).d("writeNfcTag: writingProgress->Success")
                    } catch (e: STException) {
                        Timber.tag(TAG).e(e, "writeNfcTag: ")
                        Timber.tag(TAG).i("writeNfcTag: try again count-> $retryCount")
                        innerWriteNfcBytes()
                    }
                }
                innerWriteNfcBytes()
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "writeNfcTag: ")
                Timber.tag(TAG).d("writeNfcTag: writingProgress->Fail")
                writingProgressFlow.update { NfcProgressState.Fail }
            }
        }
    }

    fun refreshNfcData() {
        coroutineScope.launch(Dispatchers.IO) {
            Timber.tag(TAG).i("refreshNfcData: currentNfcByteArray->$currentNfcByteArray")
            currentNfcByteArray ?: return@launch
            val driverModel = MappingConverter.parse(currentNfcByteArray)
            Timber.tag(TAG).i("refreshNfcData: driverModel->$driverModel")
            driverModel?.run { nfcModelFlow.update { this } }
        }
    }

    fun enableNfcReader() {
        Timber.tag(TAG).i("enableNfcReader")
        nfcModeFlow.update { NfcMode.Reader }
        readingProgressFlow.update { NfcProgressState.Idle }
        writingProgressFlow.update { NfcProgressState.Idle }
    }

    fun enableNfcWriter() {
        Timber.tag(TAG).i("enableNfcWriter")
        nfcModeFlow.update { NfcMode.Writer }
        readingProgressFlow.update { NfcProgressState.Idle }
        writingProgressFlow.update { NfcProgressState.Idle }
    }

    fun disableNfcReaderOrWriter() {
        Timber.tag(TAG).i("disableNfcReaderOrWriter")
        nfcModeFlow.update { NfcMode.Disable }
        readingProgressFlow.update { NfcProgressState.Idle }
        writingProgressFlow.update { NfcProgressState.Idle }
    }

    fun reset() {
        Timber.tag(TAG).i("reset")
        readingProgressFlow.update { NfcProgressState.Idle }
        writingProgressFlow.update { NfcProgressState.Idle }
        nfcModelFlow.update { null }
        currentNfcByteArray = null
        enableNfcReader()
    }

    fun hasSupportNfc(): Boolean =
        tryCatchReturn { AppContext.get().packageManager.hasSystemFeature(PackageManager.FEATURE_NFC) } == true

    private fun Tag.getNfcTag(): NFCTag? = tryCatchReturn { NfcTagUtils.parseTagInfo(this).nfcTag }

    private fun enableForegroundDispatch(activity: Activity) {
        Timber.tag(TAG).i("enableForegroundDispatch")
        tryCatch {
            nfcAdapter ?: return
            val intent = Intent(activity, activity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent = PendingIntent.getActivity(
                activity, 0, intent, PendingIntent.FLAG_MUTABLE
            )
            nfcAdapter?.enableForegroundDispatch(
                activity,
                pendingIntent,
                null /*nfcFiltersArray*/,
                null /*nfcTechLists*/
            )
        }
    }

}