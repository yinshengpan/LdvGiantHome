package com.ledvance.utils

import android.content.Context
import com.tencent.mmkv.MMKV
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2024/4/29 10:19
 * Describe : Storage
 */
object Storage {

    private val mmkv by lazy {
        MMKV.defaultMMKV()
    }

    fun initialize(context: Context) {
        MMKV.initialize(context)
    }

    fun <T> setValue(key: String, value: T) = with(mmkv.edit()) {
        when (value) {
            is Long -> putLong(key, value)
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            else -> putString(key, serialize(value))
        }.apply()
    }


    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(key: String, default: T): T = with(mmkv) {
        val res: Any? = when (default) {
            is Long -> getLong(key, default)
            is String -> getString(key, default)
            is Int -> getInt(key, default)
            is Boolean -> getBoolean(key, default)
            is Float -> getFloat(key, default)
            else -> deSerialization<T>(getString(key, "") ?: "")
        }
        return res as T
    }

    /**
     * 序列化对象
     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        return ByteArrayOutputStream().use { byteArrayOutputStream ->
            ObjectOutputStream(byteArrayOutputStream).use { objectOutputStream ->
                objectOutputStream.writeObject(obj)
                URLEncoder.encode(byteArrayOutputStream.toString("ISO-8859-1"), "UTF-8")
            }
        }
    }

    /**
     * 反序列化对象
     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = URLDecoder.decode(str, "UTF-8")
        return ByteArrayInputStream(redStr.toByteArray(charset("ISO-8859-1"))).use {
            ObjectInputStream(it).use {
                it.readObject() as A
            }
        }
    }
}