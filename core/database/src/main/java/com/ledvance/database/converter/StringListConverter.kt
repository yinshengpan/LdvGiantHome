package com.ledvance.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 16:14
 * Describe : StringListConverter
 */
class StringListConverter {
    private val json = Json { encodeDefaults = true }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return json.encodeToString(ListSerializer(String.serializer()), value ?: emptyList())
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return json.decodeFromString(ListSerializer(String.serializer()), value)
    }
}