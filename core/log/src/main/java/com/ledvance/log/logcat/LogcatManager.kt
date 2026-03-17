package com.ledvance.log.logcat

import com.ledvance.log.LogManager
import java.io.BufferedReader
import java.io.Closeable
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/4 15:54
 * Describe : LogcatManager
 */
internal object LogcatManager {
    fun startMonitor() {
        Thread(LogRunnable()).start()
    }

    @Throws(IOException::class)
    private fun createLogcatBufferedReader(): BufferedReader {
        clear()
        // Process process = Runtime.getRuntime().exec("/system/bin/logcat -b " + "main -P '\"/" + android.os.Process.myPid() + " 10708\"'");
        // Process process = Runtime.getRuntime().exec("/system/bin/logcat -b all -v uid");
        // Process process = Runtime.getRuntime().exec("logcat -b all -v uid");
        val process = ProcessBuilder("logcat", "-v", "threadtime").start()
        return BufferedReader(InputStreamReader(process.inputStream))
    }

    private class LogRunnable : Runnable {
        override fun run() {
            var reader: BufferedReader? = null
            var line: String?
            while (true) {
                synchronized(LogcatManager::class.java) {
                    if (reader == null) {
                        reader = try {
                            createLogcatBufferedReader()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            return
                        }
                    }
                    line = try {
                        reader?.readLine()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        closeStream(reader)
                        return
                    }
                    if (line == null || line!!.contains("read: Unexpected EOF!")) {
                        // 正常情况讲，line 是不会为空的，因为没有新的日志前提下 reader.readLine() 会阻塞读取
                        // 但是在某些特殊机型（vivo iQOO 9 Pro Android 12）上面会出现，在没有新的日志前提下，会返回 null
                        // 并且等待一会儿再读取还不行，无论循环等待多次，因为原先的流里面已经没有东西了，要读取新的日志必须创建新的流
                        try {
                            closeStream(reader)
                            reader = null
                            Thread.sleep(5000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        return@synchronized
                    }
                    LogManager.writeLogcat(line ?: "")
                }
            }
        }
    }

    private fun clear() {
        try {
            ProcessBuilder("logcat", "-c").start()
        } catch (ignored: IOException) {
        }
    }

    private fun closeStream(closeable: Closeable?) {
        if (closeable == null) {
            return
        }
        try {
            closeable.close()
        } catch (_: IOException) {
        }
    }
}