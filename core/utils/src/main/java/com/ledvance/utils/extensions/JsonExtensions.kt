package com.ledvance.utils.extensions

import kotlinx.serialization.json.Json

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/5/30 08:30
 * Describe : JsonExtensions
 */
val jsonFormatter by lazy {
    Json {
        ignoreUnknownKeys = true // 忽略多余字段
        prettyPrint = false
        isLenient = true
        encodeDefaults = true
    }
}

/**
 * 将对象序列化为 JSON 字符串
 */
fun Any?.toJson(): String? = try {
    jsonFormatter.encodeToString(this)
} catch (e: Exception) {
    e.printStackTrace()
    null
}

/**
 * 将 JSON 字符串反序列化为对象，类型自动推断
 */
inline fun <reified T> String.jsonAsOrNull(): T? = try {
    jsonFormatter.decodeFromString<T>(this)
} catch (e: Exception) {
    e.printStackTrace()
    null
}