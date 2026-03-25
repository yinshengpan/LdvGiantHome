package com.ledvance.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/27 10:59
 * Describe : FileExtensions
 */
private const val TAG = "FileExtensions"

fun File.zip(outputPath: String, filter: (String) -> Boolean): Boolean {
    val exists = exists()
    Timber.tag(TAG).i("zip begin >>>>>> input=$absolutePath > output=$outputPath exists=$exists")
    if (!exists) return false
    try {
        val outputFile = File(outputPath)
        if (outputFile.exists()) {
            outputFile.delete()
        }
        ZipOutputStream(FileOutputStream(outputFile)).use { zipOutputStream ->
            fun File.innerZip(parentDirPath: String) {
                val listFiles = listFiles()
                if (listFiles.isNullOrEmpty()) return
                for (file in listFiles) {
                    if (file.absolutePath == outputFile.absolutePath) {
                        continue
                    }
                    if (file.isFile && !filter.invoke(file.name)) {
                        Timber.tag(TAG).i("zip compress >>>>>> ${file.absolutePath}")
                        val zipEntry = ZipEntry(parentDirPath + file.name)
                        zipOutputStream.putNextEntry(zipEntry)
                        FileInputStream(file).use { inputStream ->
                            val buffer = ByteArray(1024)
                            var len: Int
                            while (inputStream.read(buffer).also { len = it } > 0) {
                                zipOutputStream.write(buffer, 0, len)
                            }
                        }
                        zipOutputStream.closeEntry()
                    } else if (file.isDirectory && !filter.invoke(file.name)) {
                        val dirPath = parentDirPath + file.name + File.separator
                        zipOutputStream.putNextEntry(ZipEntry(dirPath))
                        file.innerZip(dirPath)
                        zipOutputStream.closeEntry()
                    }
                }
            }
            innerZip("")
        }
        Timber.tag(TAG).i("zip end <<<<<< $outputPath")
        return true
    } catch (e: Exception) {
        Timber.tag(TAG).e(e, "zip input=$absolutePath , output=$outputPath")
        return false
    }
}

fun File.toUri(context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", this)
    } else {
        Uri.fromFile(this);
    }
}

fun File.share(context: Context): Boolean {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            val fileUri = toUri(context)
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(fileUri, context.contentResolver.getType(fileUri))
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        if (context.packageManager?.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            return false
        }
        context.startActivity(intent)
        return true
    } catch (e: Exception) {
        Timber.tag(TAG).e(e, "share $absolutePath")
        return false
    }
}


fun File.shareByEmail(
    context: Context,
    title: String? = null,
    email: String? = null,
    text: String? = null
): Boolean {
    try {
        val fileUri = toUri(context)
        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
        emailSelectorIntent.data = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_TITLE, title)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        emailIntent.putExtra(Intent.EXTRA_TEXT, text)
        emailIntent.selector = emailSelectorIntent
        // 添加附件
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(emailIntent)
        return true
    } catch (e: Exception) {
        Timber.tag(TAG).e(e, "shareByEmail $absolutePath")
        return false
    }
}

fun File.deleteDirFiles(predicate: (File) -> Boolean = { true }) {
    if (exists() && isDirectory) {
        listFiles().orEmpty().sortedByDescending { it.lastModified() }
            .forEach { file ->
                try {
                    if (predicate(file)) {
                        file.delete()
                    }
                } catch (_: Exception) {
                }
            }
    }
}

fun File.toByteArray(): ByteArray {
    return inputStream().use { it.readBytes() }
}

private val df by lazy {
    DecimalFormat("0.00")
}

fun File.sizeString(): String {
    val size = this.length()
    return size.sizeString()
}

fun Long.sizeString(): String {
    val size = this
    return when {
        size < 1024 -> "$size B"
        size < 1024L * 1024 -> df.format(size / 1024.0) + " KB"
        size < 1024L * 1024 * 1024 -> df.format(size / (1024.0 * 1024)) + " MB"
        size < 1024L * 1024 * 1024 * 1024 -> df.format(size / (1024.0 * 1024 * 1024)) + " GB"
        else -> df.format(size / (1024.0 * 1024 * 1024 * 1024)) + " TB"
    }
}