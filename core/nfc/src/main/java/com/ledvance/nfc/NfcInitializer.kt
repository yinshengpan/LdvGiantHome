package com.ledvance.nfc

import android.content.Context
import android.content.IntentFilter
import android.nfc.NfcAdapter
import androidx.startup.Initializer
import com.ledvance.nfc.receiver.NfcStateChangeReceiver
import com.ledvance.utils.extensions.tryCatch

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/12 16:38
 * Describe : NfcInitializer
 */
internal class NfcInitializer : Initializer<Boolean> {
    override fun create(context: Context): Boolean {
        tryCatch {
            val intentFilter = IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
            context.registerReceiver(NfcStateChangeReceiver(), intentFilter)
        }
        return true
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}