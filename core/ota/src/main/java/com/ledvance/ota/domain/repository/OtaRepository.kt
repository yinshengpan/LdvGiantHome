package com.ledvance.ota.domain.repository

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.ledvance.ota.domain.model.OtaState
import kotlinx.coroutines.flow.Flow

interface OtaRepository {
    fun startOtaUpdate(context: Context, otaDevice: BluetoothDevice, bytes: ByteArray): Flow<OtaState>
}
