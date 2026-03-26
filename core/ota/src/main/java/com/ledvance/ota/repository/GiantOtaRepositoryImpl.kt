package com.ledvance.ota.repository

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.ledvance.ota.domain.model.OtaState
import com.ledvance.ota.domain.repository.OtaRepository
import com.ledvance.utils.BluetoothManager
import com.ws.libwsota.WSOTA
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/26/26 10:57
 * Describe : GiantOtaRepositoryImpl
 */
@Singleton
class GiantOtaRepositoryImpl @Inject constructor() : OtaRepository {

    companion object {
        private const val TAG = "GiantOta"
    }

    override fun startOtaUpdate(context: Context, otaDevice: BluetoothDevice, bytes: ByteArray): Flow<OtaState> = callbackFlow {
        Timber.tag(TAG).d("OTA -> start (mac=${otaDevice.address}, size=${bytes.size})")

        var finished = false

        fun emitState(state: OtaState) {
            Timber.tag(TAG).d("OTA -> emit: $state")
            trySend(state)
        }

        fun finish(state: OtaState) {
            if (finished) return
            finished = true
            Timber.tag(TAG).d("OTA -> finish: $state")
            trySend(state)
            close()
        }

        val firmwareError = WSOTA.load(bytes)
        if (firmwareError != null) {
            Timber.tag(TAG).e("OTA -> firmware load failed $firmwareError")
            finish(OtaState.OtaFail(firmwareError))
            return@callbackFlow
        }

        Timber.tag(TAG).d("OTA -> firmware loaded")

        val callback = object : WSOTA.EventCallback {
            var updateOtaFlag = false

            override fun otaDeviceScanCallback(deviceInfo: WSOTA.DeviceInfo?, device: BluetoothDevice?, rssi: Int) {
                Timber.tag(TAG).d("Scan -> device=${device?.address}, rssi=$rssi")
                if (device?.address == otaDevice.address) {
                    Timber.tag(TAG).d("Scan -> target device found")
                    emitState(OtaState.DeviceFound(device))
                    launch {
                        WSOTA.scanStop()
                        delay(1000)
                        WSOTA.deviceConnect(context, otaDevice)
                    }
                }
            }

            override fun otaBeaconFailCallback(error: String?, errorCode: Int) {
                Timber.tag(TAG).e("Beacon -> fail (error=$error, code=$errorCode)")
                finish(OtaState.OtaFail(error ?: "beacon fail"))
            }

            override fun otaBeaconSuccessCallback() {
                Timber.tag(TAG).d("Beacon -> success")
                emitState(OtaState.BeaconSuccess)
            }

            override fun otaConnectFailCallback(error: String?, errorCode: Int) {
                Timber.tag(TAG).e("Connect -> fail (error=$error, code=$errorCode)")
                finish(OtaState.OtaFail(error ?: "connect fail"))
            }

            override fun otaConnectSuccessCallback(info: WSOTA.DeviceInfo?) {
                Timber.tag(TAG).d("Connect -> success $info")
                emitState(OtaState.ConnectSuccess(otaDevice))
                WSOTA.startUpdate(WSOTA.WORK_MODE_UPDATE)
            }

            override fun otaDeviceInfoUpdateCallback(info: WSOTA.DeviceInfo?) {
                Timber.tag(TAG).d("DeviceInfo -> updated: $info")
                emitState(OtaState.DeviceInfoUpdated)
            }

            override fun otaDisconnect(error: String?) {
                Timber.tag(TAG).d("Disconnect -> error=$error")
                if (updateOtaFlag) {
                    finish(OtaState.OtaSuccess)
                } else {
                    finish(OtaState.OtaFail(error ?: "disconnect"))
                }
            }

            override fun otaModeSetSuccessCallback() {
                Timber.tag(TAG).d("Mode -> set success")
                emitState(OtaState.ModeSetSuccess)
            }

            override fun otaModeSetFailCallback(error: String?) {
                Timber.tag(TAG).e("Mode -> set fail (error=$error)")
                emitState(OtaState.ModeSetFail)
            }

            override fun otaUpdateProgressCallback(total: Int, progress: Int) {
                val percent = if (total == 0) 0f else progress * 100f / total
                Timber.tag(TAG).d("OTA -> progress: $progress/$total ($percent%)")
                if (progress >= total) {
                    updateOtaFlag = true
                }
                emitState(OtaState.OtaProgress(percent))
            }

            override fun otaUpdateFailCallback(error: String?) {
                Timber.tag(TAG).e("OTA -> update fail (error=$error)")
                finish(OtaState.OtaFail(error ?: "update fail"))
            }

            override fun otaRegValueCallback(p0: String?, p1: Int, p2: Int, p3: Int, p4: Int) {
                Timber.tag(TAG).d("Reg -> value: [$p1,$p2,$p3,$p4]")
            }
        }

        WSOTA.setEventCallback(callback)

        BluetoothManager.ensureBleScanInterval()
        WSOTA.scanStart()

        awaitClose {
            Timber.tag(TAG).d("OTA -> cancelled / cleanup")
            runCatching {
                WSOTA.setEventCallback(null)
                WSOTA.abortUpdate()
                WSOTA.scanStop()
                WSOTA.deviceDisconnect()
            }
        }
    }.buffer(Channel.UNLIMITED)
}