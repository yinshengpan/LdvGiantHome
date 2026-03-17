package com.ledvance.nfc.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import com.ledvance.nfc.LDVNfcManager
import com.ledvance.nfc.utils.DeviceUtils
import kotlinx.coroutines.flow.update
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/12 13:19
 * Describe : NfcStateChangeReceiver
 */
internal class NfcStateChangeReceiver : BroadcastReceiver() {
    private val TAG = "NfcStateChangeReceiver"
    private val SAMSUNG_STANDARD_MODE_NO = 3
    private val SAMSUNG_CARD_MODE_NO = 5

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.takeIf { it == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED } ?: return
        val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
        val nfcEnable = state == NfcAdapter.STATE_ON || state == NfcAdapter.STATE_TURNING_ON
        Timber.tag(TAG).i("onReceive: nfc enable -> $nfcEnable")
        when (state) {
            NfcAdapter.STATE_OFF, NfcAdapter.STATE_TURNING_OFF -> {
                LDVNfcManager.nfcEnableFlow.update { false }
            }

            NfcAdapter.STATE_ON, NfcAdapter.STATE_TURNING_ON -> {
                LDVNfcManager.nfcEnableFlow.update { true }
            }

            else -> {
                val isSamsungDevice = DeviceUtils.isSamsungDevice()
                Timber.tag(TAG).i("onReceive: isSamsungDevice:$isSamsungDevice")
                if (isSamsungDevice && state == SAMSUNG_CARD_MODE_NO) {
                    LDVNfcManager.nfcEnableFlow.update { false }
                }
            }
        }
    }
}