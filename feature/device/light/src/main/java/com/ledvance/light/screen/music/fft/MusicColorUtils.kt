package com.ledvance.light.screen.music.fft

import kotlin.math.abs
import kotlin.random.Random

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 13:22
 * Describe : MusicColorUtils
 */

/**
 * Tuya音乐数据解析工具（传统方案：振幅 → 亮度 → 颜色表）
 */
object MusicColorUtils {

    private var lastBrightness = 0
    private var lastIndex = 0

    // RGB + Brightness + Percent
    private val colorParams = arrayOf(
        intArrayOf(26, 0, 0, 10, 10),
        intArrayOf(51, 31, 0, 20, 20),
        intArrayOf(61, 77, 0, 30, 30),
        intArrayOf(20, 102, 0, 40, 40),
        intArrayOf(0, 128, 51, 50, 50),
        intArrayOf(0, 153, 153, 60, 60),
        intArrayOf(0, 71, 179, 70, 70),
        intArrayOf(41, 0, 204, 80, 80),
        intArrayOf(184, 0, 230, 90, 90),
        intArrayOf(255, 0, 153, 100, 100)
    )

    /**
     * 通用亮度映射（分段函数）
     */
    private fun mapBrightness(value: Int): Int {
        return when {
            value < 0 -> 0
            value <= 7000 -> ((value - 5000) / 1000) + 1
            value <= 10000 -> ((value - 7000) / 700) + 3
            value <= 20000 -> ((value - 10000) / 1000) + 1
            value <= 30000 -> ((value - 20000) / 400) + 40
            value <= 40000 -> ((value - 30000) / 400) + 70
            value <= 48000 -> ((value - 40000) / 800) + 79
            else -> ((value - 48000) / 1000) + 90
        }
    }

    /**
     * 音频专用亮度映射（更粗粒度）
     */
    private fun mapBrightnessFromAudio(value: Int): Int {
        return when {
            value < 50000 -> 0
            value < 80000 -> 10
            value < 100000 -> 20
            value < 140000 -> 30
            value < 200000 -> 40
            value < 300000 -> 50
            value < 500000 -> 60
            value < 700000 -> 70
            value < 900000 -> 80
            value < 1200000 -> 90
            else -> 99
        }
    }

    /**
     * 随机颜色（亮度变化不大时保持）
     */
    fun getRandomColor(brightness: Int): IntArray {
        if (abs(brightness - lastBrightness) < 50) {
            lastBrightness = brightness
            return colorParams[lastIndex]
        }
        lastIndex = Random.nextInt(colorParams.size)
        lastBrightness = brightness
        return colorParams[lastIndex]
    }

    /**
     * 根据亮度取颜色
     */
    fun getColor(brightness: Int): IntArray {
        val index = (brightness / 10).coerceIn(0, colorParams.lastIndex)
        return colorParams[index]
    }

    /**
     * 解析 byte[]（Visualizer 波形）
     */
    fun parse(byteArray: ByteArray): Int {
        var sum = 0
        for (b in byteArray) {
            sum += b.toInt() and 0xFF
        }
        if (sum == 0) return -1
        return mapBrightness(sum - 100000)
    }

    /**
     * 解析 int[]（FFT / 频谱）
     */
    fun parse(intArray: IntArray): Int {
        var sum = 0
        for (i in intArray) {
            sum += i
        }
        if (sum == 0) return -1
        return mapBrightnessFromAudio(sum)
    }

    /**
     * 解析 short[]（麦克风 PCM）
     */
    fun parse(shortArray: ShortArray): Int {
        var sum = 0
        for (s in shortArray) {
            sum += abs(s.toInt())
        }
        if (sum == 0) return -1

        // 原逻辑
        return (sum - 5000) / 1000
    }
}