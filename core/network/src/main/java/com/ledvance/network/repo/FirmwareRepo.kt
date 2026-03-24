package com.ledvance.network.repo

import android.content.Context
import com.ledvance.network.NetworkModule
import com.ledvance.network.model.FirmwareInfo
import com.ledvance.utils.Storage
import com.ledvance.utils.extensions.deleteDirFiles
import com.ledvance.utils.extensions.tryCatchReturn
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/19/25 11:00
 * Describe : FirmwareRepo
 */

@Singleton
class FirmwareRepo @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val KEY_CLOUD_FIRMWARE_VERSION = "cloud_firmware_version"
    private val TAG = "FirmwareRepo"
    private val otaFileExtension = "ota"

    private val saveDir by lazy {
        File(context.filesDir, "firmware")
    }

    suspend fun syncFirmware(): Boolean = withContext(Dispatchers.IO) {
        val firmwareInfo = tryCatchReturn { getFirmwareInfo() }
        Timber.tag(TAG).i("syncFirmware firmwareInfo->$firmwareInfo")
        firmwareInfo ?: return@withContext false
        val otaMd5 = firmwareInfo.md5
        if (saveDir.exists()) {
            saveDir.listFiles()?.firstOrNull { it.extension == otaFileExtension }?.also { otaFile ->
                Timber.tag(TAG).i("syncFirmware local exits ota file->${otaFile.absolutePath}")
                if (otaFile.name.startsWith(otaMd5)) {
                    Storage.setValue(KEY_CLOUD_FIRMWARE_VERSION, firmwareInfo.fwVersion)
                    return@withContext true
                }
                saveDir.deleteDirFiles()
            }
        }
        saveDir.mkdirs()
        val otaFile = File(saveDir, "${otaMd5}.$otaFileExtension")
        val downloadFile = tryCatchReturn { downloadFile(firmwareInfo.fileUrl, otaFile) }
        val isSuccessfully = downloadFile != null
        Timber.tag(TAG)
            .i("syncFirmware isSuccessfully:$isSuccessfully,downloadFile->${downloadFile?.absolutePath}")
        if (isSuccessfully) {
            Storage.setValue(KEY_CLOUD_FIRMWARE_VERSION, firmwareInfo.fwVersion)
        }
        return@withContext downloadFile != null
    }

    fun getCloudFirmwareVersion(): String {
        return Storage.getValue(KEY_CLOUD_FIRMWARE_VERSION, "")
    }

    fun getOtaFile(): File? {
        return saveDir.takeIf { it.exists() }?.listFiles()
            ?.firstOrNull { it.extension == otaFileExtension }
    }

    private suspend fun getFirmwareInfo(): FirmwareInfo {
        return NetworkModule.firmwareApi.getFirmwareInfo()
    }

    private suspend fun downloadFile(
        url: String, saveFile: File, onProgress: (Int) -> Unit = {}
    ): File {
        Timber.tag(TAG).i("downloadFile begin->$url")
        val response = NetworkModule.firmwareApi.downloadFile(url)
        val body = response.body() ?: throw RuntimeException("Body null")

        val total = body.contentLength()
        val input = body.byteStream()

        saveFile.outputStream().use { output ->
            val buf = ByteArray(8 * 1024)
            var downloaded = 0L
            var read: Int

            while (input.read(buf).also { read = it } != -1) {
                output.write(buf, 0, read)
                downloaded += read
                val progress = (downloaded * 100 / total).toInt()
                Timber.tag(TAG).i("downloadFile progress:$progress%")
                onProgress(progress)
            }
        }
        Timber.tag(TAG).i("downloadFile end->${saveFile.absolutePath}")
        return saveFile
    }

}