package com.ledvance.ble

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import androidx.annotation.RequiresPermission
import com.ledvance.ble.constant.Constants.CLIENT_CHAR_CFG_UUID
import com.ledvance.ble.constant.Constants.DEFAULT_PART_SIZE
import com.ledvance.ble.constant.Constants.FRAME_INTERVAL_MS
import com.ledvance.ble.constant.Constants.MAX_DATA_LEN
import com.ledvance.ble.constant.Constants.RX_CHAR_UUID
import com.ledvance.ble.constant.Constants.SERVICE_UUID
import com.ledvance.ble.constant.Constants.START_FLAG
import com.ledvance.ble.constant.Constants.TX_CHAR_UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/10/25 14:20
 * Describe : DLBBleManager
 */
class DLBBleManager(
    private val ctx: Context,
    private val device: BluetoothDevice,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) {

    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)

    private var gatt: BluetoothGatt? = null
    private var rxChar: BluetoothGattCharacteristic? = null
    private var txChar: BluetoothGattCharacteristic? = null

    // Sequence number increments 0..255, wraps to 0
    @Volatile
    private var seqCounter = 0


    // For awaiting responses: map seq -> deferred parsed frame
    private val pendingResponses = mutableMapOf<Int, CompletableDeferred<ParsedFrame>>()
    private val pendingMutex = Mutex()

    // Serial write mutex - ensures only one write in flight and enforces frame interval
    private val writeMutex = Mutex()

    // Callbacks from user
    var onConnected: (() -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null
    var onLog: ((String) -> Unit)? = null
    var onHandshakeSuccess: ((ParsedFrame) -> Unit)? = null
    var onFileProgress: ((sentParts: Int, totalParts: Int) -> Unit)? = null
    var onFileCompleted: ((success: Boolean) -> Unit)? = null

    // --- Frame data model & helpers ---
    data class ParsedFrame(val seq: Int, val type: Int, val payload: ByteArray)

    private fun checksum(bytes: ByteArray): Byte {
        var sum = 0
        for (b in bytes) sum = (sum + (b.toInt() and 0xFF)) and 0xFFFF
        return (sum and 0xFF).toByte()
    }

    private fun log(s: String) {
        onLog?.invoke(s)
    }

    private fun nextSeq(): Int {
        val cur = seqCounter
        seqCounter = (seqCounter + 1) and 0xFF
        return cur
    }

    private fun buildFrame(seq: Int, frameType: Int, payload: ByteArray = ByteArray(0)): ByteArray {
        val dataLen = 1 + 1 + payload.size + 1 // seq + type + payload + cs
        require(dataLen <= MAX_DATA_LEN) { "dataLen > $MAX_DATA_LEN" }

        val buf = ByteBuffer.allocate(1 + 1 + dataLen) // start + dataLen + data...
        buf.order(ByteOrder.BIG_ENDIAN)
        buf.put(START_FLAG)
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
    private fun parseFrame(bytes: ByteArray): ParsedFrame {
        if (bytes.size < 6) throw IllegalArgumentException("frame too short")
        val bb = ByteBuffer.wrap(bytes)
        bb.order(ByteOrder.BIG_ENDIAN)
        val start = bb.get()
        if (start != START_FLAG) throw IllegalArgumentException("invalid start")
        val dataLen = bb.get().toInt() and 0xFF
        if (dataLen + 2 != bytes.size) throw IllegalArgumentException("length mismatch")
        val seq = bb.get().toInt() and 0xFF
        val type = bb.get().toInt() and 0xFF
        val payloadLen = dataLen - 3
        val payload = ByteArray(if (payloadLen > 0) payloadLen else 0)
        if (payloadLen > 0) bb.get(payload)
        val recvCs = bb.get()
        val csInput = ByteArray(1 + 1 + payload.size)
        csInput[0] = seq.toByte()
        csInput[1] = type.toByte()
        if (payload.isNotEmpty()) System.arraycopy(payload, 0, csInput, 2, payload.size)
        val calcCs = checksum(csInput)
        if (recvCs != calcCs) throw IllegalArgumentException("checksum mismatch")
        return ParsedFrame(seq, type, payload)
    }

    // --- BLE callbacks ---
    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                log("Gatt connected, discovering services...")
                g.discoverServices()
                onConnected?.invoke()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                log("Gatt disconnected")
                onDisconnected?.invoke()
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                log("Service discovery failed: $status")
                return
            }
            val svc = g.getService(SERVICE_UUID)
            if (svc == null) {
                log("Service not found")
                return
            }
            rxChar = svc.getCharacteristic(RX_CHAR_UUID)
            txChar = svc.getCharacteristic(TX_CHAR_UUID)
            if (txChar == null || rxChar == null) {
                log("Missing chars")
                return
            }
            // enable notifications
            g.setCharacteristicNotification(txChar, true)
            val desc = txChar!!.getDescriptor(CLIENT_CHAR_CFG_UUID)
            desc?.let {
                it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                g.writeDescriptor(it)
            } ?: log("TX char has no CCC descriptor")
            // try request MTU (optional)
            try {
                g.requestMtu(517)
            } catch (e: Exception) {
                // ignore
            }
            log("Service & chars ready")
        }

        override fun onDescriptorWrite(g: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            log("Descriptor write status: $status")
        }

        override fun onMtuChanged(g: BluetoothGatt, mtu: Int, status: Int) {
            log("MTU changed: $mtu status:$status")
        }

        override fun onCharacteristicWrite(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            log("onCharacteristicWrite status=$status")
            // no need to signal here - we manage higher-level through pendingResponses map per seq
        }

        override fun onCharacteristicChanged(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val data = characteristic.value ?: return
            // Called on main thread by Android, forward to coroutine processing
            scope.launch {
                try {
                    val parsed = parseFrame(data)
                    log("Received frame type=0x${parsed.type.toString(16)} seq=${parsed.seq} payloadLen=${parsed.payload.size}")
                    // deliver to awaiting deferred if exists
                    pendingMutex.withLock {
                        val d = pendingResponses.remove(parsed.seq)
                        d?.complete(parsed)
                    }
                    // special events: handshake answer
                    if (parsed.type == 0x02) {
                        onHandshakeSuccess?.invoke(parsed)
                    }
                } catch (e: Exception) {
                    log("Frame parse error: ${e.message}")
                }
            }
        }
    }

    // --- Public API: connect / disconnect ---
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect() {
        gatt = device.connectGatt(ctx, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        try {
            gatt?.disconnect()
            gatt?.close()
        } catch (_: Exception) {
        } finally {
            gatt = null
            rxChar = null
            txChar = null
        }
    }

    // --- Low level write: serializes writes, enforces FRAME_INTERVAL_MS ---
    @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    private suspend fun writeRaw(frame: ByteArray) {
        writeMutex.withLock  {
            val g = gatt ?: throw IllegalStateException("gatt null")
            val c = rxChar ?: throw IllegalStateException("rxChar null")
            c.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            c.value = frame
            val ok = g.writeCharacteristic(c)
            if (!ok) throw IllegalStateException("gatt.writeCharacteristic returned false")
            // wait small time to satisfy device frame interval requirement
            delay(FRAME_INTERVAL_MS)
        }
    }

    // High level: send a frame and optionally wait response (same seq)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private suspend fun sendFrameAndAwait(seq: Int, frameType: Int, payload: ByteArray, timeoutMs: Long = 8000L): ParsedFrame {
        val frame = buildFrame(seq, frameType, payload)
        val deferred = CompletableDeferred<ParsedFrame>()
        pendingMutex.withLock { pendingResponses[seq] = deferred }
        writeRaw(frame)
        try {
            return withTimeout(timeoutMs) { deferred.await() }
        } finally {
            // ensure removal if timed out / completed
            pendingMutex.withLock { if (pendingResponses[seq]?.isCompleted != false) pendingResponses.remove(seq) }
        }
    }

    // --- Public high-level protocol operations ---

    /**
     * sendHandshake: constructs and sends 0x01 handshake, waits for 0x02 reply.
     *
     * Fields per doc:
     * SN(16), User(1), UserId(4), startTs(4), endTs(4), clockTs(4), tzIndex(1), tzString(10)
     * (See doc for field definitions.)  [oai_citation:3‡DLB盒子家用-商用充电桩协议V3.12.docx](sediment://file_00000000d8507209ae850da264ed0e21)
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun sendHandshake(
        sn: String,
        user: Int,
        userId: Int,
        permStartTs: Int,
        permEndTs: Int,
        clockTs: Int,
        tzIndex: Int,
        tzStr: String,
        timeoutMs: Long = 8000L
    ): ParsedFrame {
        val seq = nextSeq()
        // SN fixed 16 bytes, pad 0
        val snBytes = sn.toByteArray(Charsets.US_ASCII)
        val snFixed = ByteArray(16) { 0 }
        System.arraycopy(snBytes, 0, snFixed, 0, minOf(snBytes.size, 16))

        val payload = ByteBuffer.allocate(16 + 1 + 4 + 4 + 4 + 4 + 1 + 10)
        payload.order(ByteOrder.BIG_ENDIAN)
        payload.put(snFixed)
        payload.put(user.toByte())
        payload.putInt(userId)
        payload.putInt(permStartTs)
        payload.putInt(permEndTs)
        payload.putInt(clockTs)
        payload.put(tzIndex.toByte())
        val tzB = tzStr.toByteArray(Charsets.US_ASCII)
        val tzFixed = ByteArray(10) { 0 }
        System.arraycopy(tzB, 0, tzFixed, 0, minOf(10, tzB.size))
        payload.put(tzFixed)
        log("Sending handshake seq=$seq")
        val resp = sendFrameAndAwait(seq, 0x01, payload.array(), timeoutMs)
        // resp.type should be 0x02 per doc
        if (resp.type != 0x02) throw IllegalStateException("unexpected response type ${resp.type}")
        return resp
    }

    /**
     * sendFile: implement file downlink per doc:
     * 1) send 0x70 (file down request) with file meta
     * 2) wait 0x71 ready
     * 3) send 0x72 data packages one by one; wait for 0x73 after each package and
     *    handle status / nextNeededPart per response.
     *
     * Doc: default part size 200, CRC is 2-byte sum (accumulate bytes & take 2 bytes).
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun sendFile(
        sn: String,
        fileType: Int,
        fileData: ByteArray,
        partSize: Int = DEFAULT_PART_SIZE
    ) {
        if (partSize <= 0 || partSize % 4 != 0) throw IllegalArgumentException("partSize must be multiple of 4 per doc")
        val totalLen = fileData.size
        val totalParts = (totalLen + partSize - 1) / partSize
        // calc 2-byte file CRC: accumulate all bytes, take lower 16 bits
        var sum = 0
        for (b in fileData) sum = (sum + (b.toInt() and 0xFF)) and 0xFFFF
        val crc = sum and 0xFFFF

        // 1) send 0x70
        val seq0 = nextSeq()
        val snBytes = sn.toByteArray(Charsets.US_ASCII)
        val snFixed = ByteArray(16) { 0 }
        System.arraycopy(snBytes, 0, snFixed, 0, minOf(snBytes.size, 16))
        val p70 = ByteBuffer.allocate(16 + 1 + 4 + 2 + 2 + 2) // sn + fileType + totalLen + partSize + totalParts + crc
        p70.order(ByteOrder.BIG_ENDIAN)
        p70.put(snFixed)
        p70.put(fileType.toByte())
        p70.putInt(totalLen)
        p70.putShort(partSize.toShort())
        p70.putShort(totalParts.toShort())
        p70.putShort(crc.toShort())
        log("Sending 0x70 seq=$seq0 totalLen=$totalLen parts=$totalParts crc=0x${crc.toString(16)}")
        val resp70 = try {
            sendFrameAndAwait(seq0, 0x70, p70.array(), 10000L)
        } catch (e: TimeoutCancellationException) {
            onFileCompleted?.invoke(false)
            throw e
        }
        // Expect 0x71
        if (resp70.type != 0x71) {
            onFileCompleted?.invoke(false)
            throw IllegalStateException("expected 0x71 response")
        }
        // resp70.payload: SN(16) + status(1) per doc
        val status = resp70.payload.getOrNull(16)?.toInt() ?: -1
        if (status != 0x00) {
            onFileCompleted?.invoke(false)
            throw IllegalStateException("device refused file send, status=$status")
        }

        // 2) start sending parts. Device responds with 0x73 after each 0x72
        var currentPart = 0
        var attempts = 0
        val maxAttemptsPerPart = 6

        while (currentPart < totalParts) {
            val offset = currentPart * partSize
            val size = minOf(partSize, totalLen - offset)
            val dataPart = fileData.copyOfRange(offset, offset + size)
            val seqPart = nextSeq()

            // build payload: SN(16) + fileType(1) + partNo(2) + data...
            val p72 = ByteBuffer.allocate(16 + 1 + 2 + dataPart.size)
            p72.order(ByteOrder.BIG_ENDIAN)
            p72.put(snFixed)
            p72.put(fileType.toByte())
            p72.putShort(currentPart.toShort())
            p72.put(dataPart)

            log("Sending part $currentPart seq=$seqPart size=$size")
            // send and wait for 0x73
            val resp72 = try {
                sendFrameAndAwait(seqPart, 0x72, p72.array(), 10000L)
            } catch (e: TimeoutCancellationException) {
                // retry logic
                attempts++
                log("Timeout waiting 0x73 for part $currentPart attempt $attempts")
                if (attempts >= maxAttemptsPerPart) {
                    onFileCompleted?.invoke(false)
                    throw IllegalStateException("max attempts reached for part $currentPart")
                } else {
                    // retry same part (do not advance currentPart)
                    delay(500)
                    continue
                }
            }

            // parse 0x73
            if (resp72.type != 0x73) {
                log("Unexpected resp type ${resp72.type} for part $currentPart")
                attempts++
                if (attempts >= maxAttemptsPerPart) {
                    onFileCompleted?.invoke(false)
                    throw IllegalStateException("unexpected resp types for part $currentPart")
                } else {
                    delay(200)
                    continue
                }
            }

            // resp payload per doc: SN(16) + status(1) + nextNeededPart(2)
            val rl = resp72.payload
            val st = rl.getOrNull(16)?.toInt() ?: -1
            val nextNeeded = if (rl.size >= 19) {
                ((rl[17].toInt() and 0xFF) shl 8) or (rl[18].toInt() and 0xFF)
            } else -1

            when (st) {
                0x00 -> {
                    // success; move to nextNeeded (usually current+1)
                    currentPart = if (nextNeeded >= 0) nextNeeded else currentPart + 1
                    attempts = 0
                    onFileProgress?.invoke(currentPart, totalParts)
                }
                0x01 -> {
                    // failure -> device requests same package
                    log("Device requested resend of part $currentPart")
                    // keep currentPart, maybe add backoff
                    attempts++
                    if (attempts >= maxAttemptsPerPart) {
                        onFileCompleted?.invoke(false)
                        throw IllegalStateException("max resend attempts for part $currentPart")
                    }
                    // small delay then retry
                    delay(200)
                }
                0x02 -> {
                    // failure accumulated -> stop
                    onFileCompleted?.invoke(false)
                    throw IllegalStateException("device returned status 0x02 (accumulated failure)")
                }
                0x03 -> {
                    // final success
                    onFileProgress?.invoke(totalParts, totalParts)
                    onFileCompleted?.invoke(true)
                    return
                }
                0x04 -> {
                    onFileCompleted?.invoke(false)
                    throw IllegalStateException("device final receive failed (0x04)")
                }
                else -> {
                    log("Unknown status $st")
                    attempts++
                    if (attempts >= maxAttemptsPerPart) {
                        onFileCompleted?.invoke(false)
                        throw IllegalStateException("unknown status from device")
                    }
                    delay(200)
                }
            }
        }

        // If finished loop without 0x03, still consider success if all parts sent
        onFileCompleted?.invoke(true)
    }

    // optional: helper to request mtu (if supported)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun requestMtu(mtu: Int) {
        gatt?.requestMtu(mtu)
    }

    // cancel all pending
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun close() {
        scope.cancel()
        disconnect()
    }
}