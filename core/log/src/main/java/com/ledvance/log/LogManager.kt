package com.ledvance.log

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.ledvance.log.crash.registerAppCrashLog
import com.ledvance.log.logcat.LogcatManager
import com.ledvance.utils.extensions.deleteDirFiles
import com.ledvance.utils.extensions.getAppName
import com.ledvance.utils.extensions.getFirstInstallTime
import com.ledvance.utils.extensions.getLastUpdateTime
import com.ledvance.utils.extensions.getMetaData
import com.ledvance.utils.extensions.getVersionCode
import com.ledvance.utils.extensions.getVersionName
import com.ledvance.utils.extensions.share
import com.ledvance.utils.extensions.shareByEmail
import com.ledvance.utils.extensions.toTimeStr
import com.ledvance.utils.extensions.zip
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/4 10:33
 * Describe : LogManager
 * https://github.com/Tencent/mars
 */
object LogManager {
    private const val TAG = "LogManager"
    private const val MSG_FLUSH_LOG = 1001
    private const val MSG_CLEAR_LOG_CACHE = 1002
    private const val LOG_MIX_SIZE = 25 * 1024 * 1024
    private const val LOG_DIR_NAME = "log"
    private const val LOGCAT_DIR_NAME = "logcat"
    private const val CACHE_DIR_NAME = "cache"
    private var xlogDir: String? = null
    private var mFlushLogHandler: Handler? = null
    private const val PUB_KEY =
        "1367836e0b33b576b7449cb416835b74c8b41352e2642d5a24aa8aa3d46ff7c9a3911b2344200e00d057b19d5cd2a132a39298e7c1a4650b5c8834bbc661d036"
    private var mLogcatInstance: Log.LogInstance? = null

    internal fun initialize(context: Context, enableLogcat: Boolean = false) {
        xlogDir = getXlogDir(context)?.absolutePath
        enableLog(context)
        if (enableLogcat) {
            enableLogcat(context)
        }
        registerAppCrashLog()
        initFlushLogHandler()
    }

    private fun enableLog(context: Context) {
        try {
            val cacheDir = getXlogDir(context, CACHE_DIR_NAME) ?: return
            val logDir = getXlogDir(context, LOG_DIR_NAME) ?: return
            // level: 日志级别，变量见 Xlog.java 里 LEVEL_XX， Debug版本推荐 LEVEL_DEBUG， Release 版本推荐 LEVEL_INFO。
            // mode : 文件写入模式，分异步和同步，变量定义见 Xlog.java 里 AppednerModeXX， Release版本一定要用 AppednerModeAsync， Debug 版本两个都可以，但是使用 AppednerModeSync 可能会有卡顿。
            // cacheDir : 缓存目录
            // logDir : 日志写入目录，请给单独的目录，除了日志文件不要把其他文件放入该目录，不然可能会被日志的自动清理功能清理掉。
            // nameprefix : 日志文件名的前缀，例如该值为TEST，生成的文件名为：TEST_20170102.xlog。
            // cacheDays : 一般情况下填0即可。非0表示会在 _cachedir 目录下存放几天的日志。
            Log.setLogImp(Xlog())
            Xlog.open(
                true,
                Xlog.LEVEL_ALL,
                Xlog.AppednerModeAsync,
                cacheDir.absolutePath,
                logDir.absolutePath,
                "log",
                PUB_KEY
            )
            // 编译的时候在源码里面隐藏控制台打印，此方法设置存在一些问题
            // Log.setConsoleLogOpen(false)
            Timber.plant(XLogTree())
            Timber.tag(TAG).i("initialize Xlog log successfully >>> $logDir")
            Timber.i("SYSTEM: %s", Log.getSysInfo())
            Timber.i("APP: %s", getAppInfo(context))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "initialize Xlog log failed!")
        }
    }

    private fun enableLogcat(context: Context) {
        try {
            val cacheDir = getXlogDir(context, CACHE_DIR_NAME) ?: return
            val logcatDir = getXlogDir(context, LOGCAT_DIR_NAME) ?: return
            mLogcatInstance = Log.openLogInstance(
                Xlog.LEVEL_NONE,
                Xlog.AppednerModeAsync,
                cacheDir.absolutePath,
                logcatDir.absolutePath,
                "logcat",
                3,
                PUB_KEY
            )
            LogcatManager.startMonitor()
            Timber.tag(TAG).d("initialize Xlog logcat successfully >>> $logcatDir")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "initialize logcat failed!")
        }
    }

    internal fun writeLog(level: Int, tag: String, message: String) {
        when (level) {
            android.util.Log.VERBOSE -> Log.v(tag, message)
            android.util.Log.DEBUG -> Log.d(tag, message)
            android.util.Log.INFO -> Log.i(tag, message)
            android.util.Log.WARN -> Log.w(tag, message)
            android.util.Log.ERROR -> Log.e(tag, message)
            else -> Log.d(tag, message)
        }
    }

    internal fun writeLogcat(line: String) {
        mLogcatInstance?.write(line)
    }

    internal fun write(line: String) {
        Log.write(line)
    }

    private fun getAppInfo(context: Context): String {
        val sb = StringBuilder()
        sb.append("AppName:[").append(context.getAppName())
        sb.append("] PackageName:[").append(context.packageName)
        sb.append("] VersionName:[").append(context.getVersionName())
        sb.append("] VersionCode:[").append(context.getVersionCode())
        sb.append("] FirstInstallTime:[").append(context.getFirstInstallTime()?.toTimeStr())
        sb.append("] LastUpdateTime:[").append(context.getLastUpdateTime()?.toTimeStr())
        sb.append("] CommitId:[").append(context.getMetaData("git_commit_id"))
        sb.append("]")
        return sb.toString()
    }

    fun release() {
        Timber.tag(TAG).d("release")
        flushLog()
        mFlushLogHandler?.removeCallbacksAndMessages(null)
        Log.appenderClose()
    }

    private fun initFlushLogHandler() {
        val handlerThread = HandlerThread("flush-log")
        handlerThread.start()
        mFlushLogHandler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MSG_FLUSH_LOG -> {
                        flushLog()
                        sendFlushLogMessage()
                    }

                    MSG_CLEAR_LOG_CACHE -> {
                        checkLogMaxSize()
                    }

                    else -> {}
                }
            }
        }
        sendClearLogCacheMessage()
        sendFlushLogMessage()
    }

    private fun sendClearLogCacheMessage() {
        val message = Message.obtain(mFlushLogHandler, MSG_CLEAR_LOG_CACHE)
        mFlushLogHandler?.sendMessage(message)
    }

    private fun sendFlushLogMessage() {
        val message = Message.obtain(mFlushLogHandler, MSG_FLUSH_LOG)
        // 把内存里面的日志刷新到本地的间隔时间,一分钟刷新一次
        mFlushLogHandler?.sendMessageDelayed(message, 60 * 1000L)
    }

    internal fun flushLog() {
        Log.appenderFlush()
        mLogcatInstance?.appenderFlush()
    }

    private fun getXlogDir(context: Context, child: String? = null): File? {
        return try {
            val logDir = File(context.filesDir, "xlog")
            if (!logDir.exists()) {
                logDir.mkdirs()
            }
            if (child.isNullOrEmpty()) {
                logDir
            } else {
                val childDir = File(logDir, child)
                if (!childDir.exists()) {
                    childDir.mkdirs()
                }
                childDir
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "getXlogDir")
            null
        }
    }

    suspend fun getLogZipFile(context: Context): File? = withContext(Dispatchers.IO) {
        flushLog()
        val xlogDir = getXlogDir(context) ?: run {
            return@withContext null
        }
        checkLogMaxSize()
        val outputFile = File(xlogDir.absolutePath + File.separator + "AppLog.zip")
        val isZipSuccess = xlogDir.zip(outputFile.absolutePath) {
            it.endsWith(".mmap3") || it == "cache"
        }
        Timber.tag(TAG)
            .i("shareLog isZipSuccess=$isZipSuccess,outputZipPath=${outputFile.absolutePath}")
        if (isZipSuccess) {
            return@withContext outputFile
        } else {
            Timber.tag(TAG).i("log compress failed!")
            return@withContext null
        }
    }

    sealed interface ShareAppLogType {
        data class Email(val email: String, val title: String) : ShareAppLogType
        data object Other : ShareAppLogType
    }

    suspend fun shareAppLog(context: Context, type: ShareAppLogType = ShareAppLogType.Other) {
        val file = getLogZipFile(context) ?: return
        when (type) {
            is ShareAppLogType.Email -> {
                file.shareByEmail(
                    context = context,
                    title = type.title,
                    email = type.email
                )
            }

            ShareAppLogType.Other -> {
                file.share(context)
            }
        }
    }

    private fun checkLogMaxSize() {
        val path = xlogDir?.takeIf { it.isNotBlank() } ?: return
        val xlogDirFile = File(path)
        if (xlogDirFile.exists().not()) return
        xlogDirFile.listFiles().orEmpty().forEach { file ->
            try {
                when {
                    file.name == LOG_DIR_NAME || file.name == LOGCAT_DIR_NAME -> {
                        var sum = 0L
                        file.deleteDirFiles {
                            sum += it.length()
                            sum > LOG_MIX_SIZE
                        }
                    }

                    file.name.endsWith(".zip") -> {
                        file.delete()
                    }

                    else -> {}

                }
            } catch (_: Exception) {
            }
        }
    }
}