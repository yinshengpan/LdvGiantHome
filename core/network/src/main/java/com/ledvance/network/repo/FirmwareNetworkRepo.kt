package com.ledvance.network.repo

import android.content.Context
import com.ledvance.domain.FirmwareLatest
import com.ledvance.domain.FirmwareVersion
import com.ledvance.network.NetworkModule
import com.ledvance.network.model.FirmwareInfo
import com.ledvance.network.model.toDeviceType
import com.ledvance.utils.extensions.deleteDirFiles
import com.ledvance.utils.extensions.tryCatchReturn
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
class FirmwareNetworkRepo @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val TAG = "FirmwareNetworkRepo"
    private val otaFileExtension = "bin"

    private val saveDir by lazy {
        File(context.filesDir, "firmware")
    }

    suspend fun syncFirmwares(): List<FirmwareLatest> = withContext(Dispatchers.IO) {
        val firmwareList = tryCatchReturn { getFirmwareList() }
        Timber.tag(TAG).i("syncFirmware firmwareList->$firmwareList")
        firmwareList ?: return@withContext listOf()
        return@withContext firmwareList.map { async { it.syncFirmware() } }
            .awaitAll()
            .filterNotNull()
    }

    private suspend fun FirmwareInfo.syncFirmware(): FirmwareLatest? = withContext(Dispatchers.IO) {
        val deviceType = this@syncFirmware.type?.toDeviceType() ?: return@withContext null
        val version = this@syncFirmware.fwVersion.toIntOrNull() ?: return@withContext null
        val otaMd5 = md5
        val firmwareLatest = FirmwareLatest(
            deviceType = deviceType,
            latestVersion = FirmwareVersion.create(version),
            firmwareFilePath = "",
            firmwareUrl = fileUrl,
            firmwareMd5 = otaMd5,
            firmwareFileSize = 0
        )
        saveDir.mkdirs()
        val saveOtaDir = File(saveDir, deviceType.name)
        if (saveOtaDir.exists()) {
            saveOtaDir.listFiles()?.firstOrNull { it.extension == otaFileExtension }?.also { otaFile ->
                Timber.tag(TAG).i("syncFirmware local exits ota file->${otaFile.absolutePath}")
                if (otaFile.name.startsWith(otaMd5)) {
                    return@withContext firmwareLatest.copy(
                        firmwareFilePath = otaFile.absolutePath,
                        firmwareFileSize = otaFile.length()
                    )
                }
                saveOtaDir.deleteDirFiles()
            }
        }
        saveOtaDir.mkdirs()
        val otaFile = File(saveOtaDir, "${otaMd5}.$otaFileExtension")
        val downloadFile = tryCatchReturn { downloadFile(this@syncFirmware.fileUrl, otaFile) }
        val isSuccessfully = downloadFile != null
        Timber.tag(TAG)
            .i("syncFirmware isSuccessfully:$isSuccessfully,downloadFile->${downloadFile?.absolutePath}")
        return@withContext downloadFile?.let {
            firmwareLatest.copy(firmwareFileSize = it.length(), firmwareFilePath = it.absolutePath)
        } ?: firmwareLatest
    }

    private suspend fun getFirmwareList(): List<FirmwareInfo> {
        return NetworkModule.firmwareApi.getFirmwareList()
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