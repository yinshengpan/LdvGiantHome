package com.ledvance.ble

import android.Manifest
import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.RequiresPermission
import com.ledvance.ble.bean.BleCommand
import com.ledvance.ble.bean.DeviceConfiguration
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.ble.constant.BleDataIndex
import com.ledvance.ble.constant.Constants
import com.ledvance.utils.extensions.toHex
import com.ledvance.utils.extensions.toInt
import com.ledvance.utils.extensions.toUnsignedInt
import com.ledvance.utils.extensions.tryCatchReturn
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import no.nordicsemi.android.kotlin.ble.core.errors.DeviceDisconnectedException
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CopyOnWriteArrayList

class DLBBleClient(
    val device: ScannedDevice,
    private val gatt: ClientBleGatt,
    private val scope: CoroutineScope,
    private val rxChar: ClientBleGattCharacteristic,
    private val txChar: ClientBleGattCharacteristic,
    private val onTxCharDataCallback: (ParsedFrame) -> Unit = {},
) {
    private val TAG = "DLBBleClient"

    // For awaiting responses: map seq -> deferred parsed frame
    private val pendingResponses = mutableMapOf<Int, CompletableDeferred<ParsedFrame>>()
    private val pendingMutex = Mutex()

    @Volatile
    private var seqCounter = 0

    private val handlerThread = HandlerThread("BlePacketAssembler").apply { start() }
    private val handler = Handler(handlerThread.looper)
    private val dataList = CopyOnWriteArrayList<Byte>()

    init {
        scope.launch {
            txChar.getNotifications().buffer().onEach {
                val bytes = it.value
                val startFlag = bytes.firstOrNull()
                Timber.tag(TAG).i("getNotifications Received frame ${bytes.toHexString()}")
                if (startFlag == Constants.START_FLAG) {
                    dataList.clear()
                } else if (dataList.isEmpty()) {
                    return@onEach
                }
                dataList.addAll(it.value.asList())
                handler.removeCallbacks(timeoutRunnable)
                handler.postDelayed(timeoutRunnable, 2000)
                checkDataComplete()
            }.catch {
                Timber.tag(TAG).e(it, "getNotifications")
            }.flowOn(Dispatchers.IO).launchIn(scope)
        }
    }


    private fun checkDataComplete() {
        if (dataList.size < 2) return
        val dataLength = dataList[1].toUnsignedInt()
        // 68 len seq type msg... chk
        val expectedTotalLength = 1 + 1 + dataLength
        Timber.tag(TAG)
            .i("checkDataComplete dataList.size=${dataList.size} expectedTotalLength=$expectedTotalLength")
        if (dataList.size >= expectedTotalLength) {
            handler.removeCallbacks(timeoutRunnable)
            handleResult(dataList.toByteArray())
            dataList.clear()
        }
    }

    private val timeoutRunnable = Runnable {
        Timber.tag(TAG).w("handleResult timeout")
        dataList.clear()
    }

    private fun handleResult(bytes: ByteArray) {
        Timber.tag(TAG).i("handleResult data ->${bytes.toHexString()}")
        scope.launch {
            val parsed = tryCatchReturn {
                parseFrame(bytes)
            }
            parsed ?: return@launch
            Timber.tag(TAG)
                .i("handleResult frame type=0x${parsed.type.toString(16)} seq=${parsed.seq} payloadLen=${parsed.payload.size}")
            pendingMutex.withLock {
                val d = pendingResponses.remove(parsed.seq)
                d?.complete(parsed)
            }
            onTxCharDataCallback.invoke(parsed)
        }
    }

    fun disconnect() = gatt.disconnect()

    fun isConnected() = tryCatchReturn { gatt.isConnected } ?: false

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun writeFrame(frame: ByteArray) {
        delay(Constants.FRAME_INTERVAL_MS)
        Timber.tag(TAG).i("writeFrame: ${frame.toHexString()}")
        rxChar.write(DataByteArray(frame))
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun sendHandshake(
        user: Int,
        userId: Int,
        permStartTs: Int,
        permEndTs: Int,
        clockTs: Int,
        tzIndex: Int,
        tzStr: String,
        timeoutMs: Long = 8000L
    ): ParsedFrame {
        Timber.tag(TAG)
            .i("sendHandshake begin ->user:$user,userId:$userId,permStartTs:$permStartTs,permEndTs:$permEndTs,clockTs:$clockTs,tzIndex:$tzIndex,tzStr:$tzStr")
        val seq = nextSeq()

        val payload = ByteBuffer.allocate(16 + 1 + 4 + 4 + 4 + 4 + 1 + 10)
        payload.order(ByteOrder.BIG_ENDIAN)
        payload.put(getSNByteArray())
        payload.put(user.toByte())
        payload.putInt(userId)
        payload.putInt(permStartTs)
        payload.putInt(permEndTs)
        payload.putInt(clockTs)
        payload.put(tzIndex.toByte())
        val tzB = tzStr.takeIf { it.isNotEmpty() }?.toByteArray(Charsets.US_ASCII) ?: byteArrayOf()
        val tzFixed = ByteArray(10) { 0 }
        System.arraycopy(tzB, 0, tzFixed, 0, minOf(10, tzB.size))
        payload.put(tzFixed)
        val resp =
            sendFrameAndAwait(seq, BleCommand.Handshake.requestFlag, payload.array(), timeoutMs)
        // resp.type should be 0x02 per doc
        if (resp.type != BleCommand.Handshake.responseFlag) throw IllegalStateException("unexpected response type ${resp.type}")
        return resp
    }

    suspend fun editChargerPile(chargeNumber: String, isDelete: Boolean): ParsedFrame {
        Timber.tag(TAG).i("editChargerPile begin ->chargeNumber:$chargeNumber,isDelete:$isDelete")
        val seq = nextSeq()

        val payload = ByteBuffer.allocate(16 + 1 + 16)
        payload.order(ByteOrder.BIG_ENDIAN)
        payload.put(getSNByteArray())
        // 0：增加 1：删除
        val status = if (isDelete) 1 else 0
        payload.put(status.toByte())

        val numberArray =
            chargeNumber.takeIf { it.isNotEmpty() }?.toByteArray(Charsets.US_ASCII) ?: byteArrayOf()
        val numberFixed = ByteArray(16) { 0 }
        System.arraycopy(numberArray, 0, numberFixed, 0, minOf(16, numberArray.size))
        payload.put(numberFixed)

        val resp =
            sendFrameAndAwait(seq, BleCommand.EditChargingPile.requestFlag, payload.array(), 30000)
        if (resp.type != BleCommand.EditChargingPile.responseFlag) throw IllegalStateException("unexpected response type ${resp.type}")
        return resp
    }

    suspend fun getChargerPile(user: Int, userId: Int): ParsedFrame {
        Timber.tag(TAG).i("getChargerPile begin ->user:$user,userId:$userId")
        val seq = nextSeq()
        val payload = ByteBuffer.allocate(16 + 1 + 4)
        payload.order(ByteOrder.BIG_ENDIAN)
        payload.put(getSNByteArray())
        payload.put(user.toByte())
        payload.putInt(userId)
        val resp = sendFrameAndAwait(seq, BleCommand.GetChargerPile.requestFlag, payload.array())
        if (resp.type != BleCommand.GetChargerPile.responseFlag) throw IllegalStateException("unexpected response type ${resp.type}")
        return resp
    }

    suspend fun getConfiguration(configuration: DeviceConfiguration): ParsedFrame {
        Timber.tag(TAG).i("getConfiguration begin ->name:${configuration.name}")
        val seq = nextSeq()
        val nameLength = configuration.name.length
        val payload = ByteBuffer.allocate(16 + 2 + nameLength)
        payload.order(ByteOrder.BIG_ENDIAN)
        payload.put(getSNByteArray())
        payload.putShort(nameLength.toShort())
        payload.put(configuration.name.toByteArray(Charsets.US_ASCII))
        val resp =
            sendFrameAndAwait(seq, BleCommand.GetConfiguration.requestFlag, payload.array())
        if (resp.type != BleCommand.GetConfiguration.responseFlag) throw IllegalStateException("unexpected response type ${resp.type}")
        return resp
    }

    suspend fun setConfiguration(configuration: DeviceConfiguration, value: String): ParsedFrame {
        Timber.tag(TAG).i("setConfiguration begin ->${configuration.name}:$value")
        val seq = nextSeq()
        val nameLength = configuration.name.length
        val valueLength = value.length
        val payload = ByteBuffer.allocate(16 + 2 + 2 + nameLength + valueLength)
        payload.order(ByteOrder.BIG_ENDIAN)
        payload.put(getSNByteArray())
        payload.putShort(nameLength.toShort())
        payload.putShort(valueLength.toShort())
        payload.put(configuration.name.toByteArray(Charsets.US_ASCII))
        payload.put(value.toByteArray(Charsets.US_ASCII))
        val resp =
            sendFrameAndAwait(seq, BleCommand.SetConfiguration.requestFlag, payload.array())
        if (resp.type != BleCommand.SetConfiguration.responseFlag) throw IllegalStateException("unexpected response type ${resp.type}")
        return resp
    }

    private fun getSNByteArray(): ByteArray {
        // SN fixed 16 bytes, pad 0
        val snBytes = byteArrayOf()
        val snFixed = ByteArray(16) { 0 }
        System.arraycopy(snBytes, 0, snFixed, 0, minOf(snBytes.size, 16))
        return snFixed
    }

    suspend fun sendOtaFile(
        fileBytes: ByteArray,
        onFileProgress: (Int, Int) -> Unit = { _, _ -> }
    ): Boolean {
        Timber.tag(TAG).i("sendOtaFile begin ->${fileBytes.size}")
        val partSize = 200
        // 先发送文件相关信息
        val isSuccessfully = sendOtaFileInfo(fileBytes, partSize)
        if (!isSuccessfully) {
            return false
        }
        val totalLen = fileBytes.size
        val totalParts = (totalLen + partSize - 1) / partSize
        var currentPart = 0
        var attempts = 0
        val maxAttemptsPerPart = 6
        Timber.tag(TAG).i("sendOtaFile totalLen:$totalLen partSize:$partSize")
        onFileProgress.invoke(currentPart, totalParts)
        while (currentPart < totalParts) {
            val offset = currentPart * partSize
            val size = minOf(partSize, totalLen - offset)
            val dataPart = fileBytes.copyOfRange(offset, offset + size)
            val result = tryCatchReturn {
                sendOtaFilePart(dataPart, currentPart)
            }
            val status = result?.first
            val nextNeeded = result?.second ?: -1
            // 0x00 成功，0x01 失败,0x02失败累计到一定次数收到此结果，APP不在发送文件，0x03接收成功，0x04接收失败
            if (status == 0x04 || status == 0x03 || status == 0x02) {
                Timber.tag(TAG).i("sendOtaFile end status->$status")
                return status == 0x03
            }
            when (status) {
                0x00 -> {
                    currentPart = if (nextNeeded >= 0) nextNeeded else currentPart + 1
                    attempts = 0
                    Timber.tag(TAG).i("sendOtaFile progress->$currentPart/$totalParts")
                    onFileProgress.invoke(currentPart, totalParts)
                }

                0x01 -> {
                    attempts++
                    if (attempts >= maxAttemptsPerPart) {
                        throw IllegalStateException("max resend attempts for part $currentPart")
                    }
                    Timber.tag(TAG).e("sendOtaFile 0x01 failed!($attempts)")
                    delay(200)
                }

                else -> {
                    attempts++
                    if (attempts >= maxAttemptsPerPart) {
                        throw IllegalStateException("max resend attempts for part $currentPart")
                    }
                    Timber.tag(TAG).e("sendOtaFile unknown failed!($attempts)")
                    delay(200)
                }
            }
        }
        return true
    }

    private suspend fun sendOtaFileInfo(fileBytes: ByteArray, partSize: Int): Boolean {
        Timber.tag(TAG).i("sendOtaFileInfo begin")
        val totalLen = fileBytes.size
        val payload = ByteBuffer.allocate(16 + 1 + 4 + 2 + 2 + 2)
        payload.order(ByteOrder.BIG_ENDIAN)
        payload.put(getSNByteArray())
        // 0x00 升级包，0x01图片
        payload.put(0x00)
        payload.putInt(totalLen)
        payload.putShort(partSize.toShort())//默认每包200个字节
        val totalParts = (totalLen + partSize - 1) / partSize
        payload.putShort(totalParts.toShort())// 文件总包数
        var sum = 0
        for (b in fileBytes) sum = (sum + (b.toInt() and 0xFF)) and 0xFFFF
        val crc = sum and 0xFFFF
        payload.putShort(crc.toShort())
        val resp = sendFrameAndAwait(nextSeq(), BleCommand.FileInfo.requestFlag, payload.array())
        if (resp.type != BleCommand.FileInfo.responseFlag) {
            return false
        }
        val data = resp.payload
        var index = 0
        // DLB盒子 SN (16字节 ASCII)
        val snBytes = data.copyOfRange(index, index + 16)
        val sn = snBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
        index += 16
        Timber.tag(TAG).i("sendOtaFileInfo result sn->${sn} ${snBytes.toHex()}")

        val statusByte = data[index++]
        val status = statusByte.toInt() and 0xFF
        Timber.tag(TAG).i("sendOtaFileInfo result status->${status} ${statusByte.toHex()}")

        // 0x00 成功，0x01 失败
        val isSuccessfully = status == 0x00
        Timber.tag(TAG).i("sendOtaFileInfo end sn$sn,isSuccessfully:$isSuccessfully")
        return isSuccessfully
    }

    private suspend fun sendOtaFilePart(partBytes: ByteArray, partIndex: Int): Pair<Int, Int> {
        val partSize = partBytes.size
        val payload = ByteBuffer.allocate(16 + 1 + 2 + partSize)
        payload.order(ByteOrder.BIG_ENDIAN)
        payload.put(getSNByteArray())
        // 0x00 升级包，0x01图片
        payload.put(0.toByte())
        payload.putShort(partIndex.toShort())
        payload.put(partBytes)
        Timber.tag(TAG)
            .i("sendOtaFilePart partIndex:$partIndex,partSize:$partSize hex:${partBytes.toHex()}")
        val resp = sendFrameAndAwait(nextSeq(), BleCommand.File.requestFlag, payload.array())
        if (resp.type != BleCommand.File.responseFlag) {
            return 0x01 to -1
        }

        val data = resp.payload
        var index = 0
        // DLB盒子 SN (16字节 ASCII)
        val snBytes = data.copyOfRange(index, index + 16)
        val sn = snBytes.toString(Charsets.US_ASCII).trimEnd('\u0000')
        index += 16
        Timber.tag(TAG)
            .i("sendOtaFilePart(partIndex:$partIndex) result sn->${sn} ${snBytes.toHex()}")

        // 0x00 成功，0x01 失败,0x02失败累计到一定次数收到此结果，APP不在发送文件，0x03接收成功，0x04接收失败
        val statusByte = data[index++]
        val status = statusByte.toInt() and 0xFF
        Timber.tag(TAG)
            .i("sendOtaFilePart(partIndex:$partIndex) result status->${status} ${statusByte.toHex()}")

        val nextPackageNumberBytes = data.copyOfRange(index, index + 2)
        val nextPackageNumber = nextPackageNumberBytes.toInt()
        index += 2
        Timber.tag(TAG)
            .i("sendOtaFilePart(partIndex:$partIndex) result nextPackageNumber->${nextPackageNumber} ${nextPackageNumberBytes.toHex()}")

        Timber.tag(TAG)
            .i("sendOtaFilePart sn$sn,status:$status,nextPackageNumber:$nextPackageNumber")
        return status to nextPackageNumber
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private suspend fun sendFrameAndAwait(
        seq: Int,
        frameType: Int,
        payload: ByteArray,
        timeoutMs: Long = 8000L
    ): ParsedFrame {
        Timber.tag(TAG).i("sendFrameAndAwait begin")
        val frame = buildFrame(seq, frameType, payload)
        val deferred = CompletableDeferred<ParsedFrame>()
        pendingMutex.withLock { pendingResponses[seq] = deferred }
        try {
            writeFrame(frame)
            return withTimeout(timeoutMs) { deferred.await() }
        } catch (e: DeviceDisconnectedException) {
            throw e
        } finally {
            // ensure removal if timed out / completed
            pendingMutex.withLock {
                if (pendingResponses[seq]?.isCompleted != false) pendingResponses.remove(
                    seq
                )
            }
            Timber.tag(TAG).i("sendFrameAndAwait end")
        }
    }

    private fun buildFrame(seq: Int, frameType: Int, payload: ByteArray = ByteArray(0)): ByteArray {
        val dataLen = 1 + 1 + payload.size + 1 // seq + type + payload + cs
        require(dataLen <= Constants.MAX_DATA_LEN) { "dataLen > ${Constants.MAX_DATA_LEN}" }

        val buf = ByteBuffer.allocate(1 + 1 + dataLen) // start + dataLen + data...
        buf.order(ByteOrder.BIG_ENDIAN)
        buf.put(Constants.START_FLAG)
        buf.put(dataLen.toByte())
        buf.put(seq.toByte())
        buf.put(frameType.toByte())
        if (payload.isNotEmpty()) buf.put(payload)

        val csInput = ByteArray(1 + 1 + payload.size)
        csInput[0] = seq.toByte()
        csInput[1] = frameType.toByte()
        if (payload.isNotEmpty()) System.arraycopy(payload, 0, csInput, 2, payload.size)
        buf.put(checksum(csInput))
        return buf.array()
    }

    @Throws(IllegalArgumentException::class)
    private fun parseFrame(bytes: ByteArray): ParsedFrame? {
        Timber.tag(TAG).i("parseFrame() ${bytes.toHexString()} ")
        if (bytes.size < 6) return let {
            Timber.tag(TAG).w("parseFrame() frame too short ${bytes.toHexString()} ")
            null
        }
        val startFlag = bytes[BleDataIndex.StartFlag.index]
        if (startFlag != Constants.START_FLAG) {
            Timber.tag(TAG).w("parseFrame() invalid header ${bytes.toHexString()} ")
            return null
        }
        val dataLen = bytes[BleDataIndex.Length.index].toInt() and 0xFF
        if (dataLen + 2 != bytes.size) {
            Timber.tag(TAG).w("parseFrame() length mismatch ${bytes.toHexString()} ")
        }
        val seq = bytes[BleDataIndex.Sequence.index].toInt() and 0xFF
        val type = bytes[BleDataIndex.FrameType.index].toInt() and 0xFF
        val payload = bytes.sliceArray(4 until bytes.lastIndex)
        val recvCs = bytes.last()
        Timber.tag(TAG)
            .i("""parseFrame startFlag:${startFlag.toHexString()},dataLen:$dataLen,seq:${seq},type:$type,payload:${payload.toHexString()},recvCs:${recvCs.toHexString()}""")
        val csInput = ByteArray(1 + 1 + payload.size)
        csInput[0] = seq.toByte()
        csInput[1] = type.toByte()
        if (payload.isNotEmpty()) System.arraycopy(payload, 0, csInput, 2, payload.size)
        val calcCs = checksum(csInput)
        if (recvCs != calcCs) {
            Timber.tag(TAG)
                .e("parseFrame() checksum mismatch recvCs:${recvCs.toHexString()} calcCs:${calcCs.toHexString()},csInput:${csInput.toHexString()}")
            return null
        }
        return ParsedFrame(seq, type, dataLen, payload)
    }

    private fun nextSeq(): Int {
        val cur = seqCounter
        seqCounter = (seqCounter + 1) and 0xFF
        return cur
    }

    private fun checksum(bytes: ByteArray): Byte {
        var sum = 0
        for (b in bytes) sum = (sum + (b.toInt() and 0xFF)) and 0xFFFF
        return (sum and 0xFF).toByte()
    }

    data class ParsedFrame(val seq: Int, val type: Int, val length: Int, val payload: ByteArray)

}