package com.ledvance.utils.extensions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.ledvance.utils.AppContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/5/30 08:43
 * Describe : DataStoreExtensions
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "nfc_driver_preferences",
    corruptionHandler = ReplaceFileCorruptionHandler {
        emptyPreferences()
    }
)

fun Preferences.Key<Boolean>.getBoolean(): Flow<Boolean?> {
    return AppContext.get().dataStore.data.map { it[this] }
}

fun Preferences.Key<Int>.getInt(): Flow<Int?> {
    return AppContext.get().dataStore.data.map { it[this] }
}

fun Preferences.Key<Long>.getLong(): Flow<Long?> {
    return AppContext.get().dataStore.data.map { it[this] }
}

fun Preferences.Key<Float>.getFloat(): Flow<Float?> {
    return AppContext.get().dataStore.data.map { it[this] }
}

fun Preferences.Key<String>.getString(): Flow<String?> {
    return AppContext.get().dataStore.data.map { it[this] }
}

fun Preferences.Key<Set<String>>.getSetString(): Flow<Set<String>?> {
    return AppContext.get().dataStore.data.map { it[this] }
}

fun Preferences.Key<ByteArray>.getByteArray(): Flow<ByteArray?> {
    return AppContext.get().dataStore.data.map { it[this] }
}

suspend fun Preferences.Key<String>.setString(value: String) {
    AppContext.get().dataStore.edit { preferences ->
        preferences[this] = value
    }
}

suspend fun Preferences.Key<Int>.setInt(value: Int) {
    AppContext.get().dataStore.edit { preferences ->
        preferences[this] = value
    }
}

suspend fun Preferences.Key<Long>.setLong(value: Long) {
    AppContext.get().dataStore.edit { preferences ->
        preferences[this] = value
    }
}

suspend fun Preferences.Key<Double>.setDouble(value: Double) {
    AppContext.get().dataStore.edit { preferences ->
        preferences[this] = value
    }
}

suspend fun Preferences.Key<Boolean>.setBoolean(value: Boolean) {
    AppContext.get().dataStore.edit { preferences ->
        preferences[this] = value
    }
}

suspend fun Preferences.Key<Float>.setFloat(value: Float) {
    AppContext.get().dataStore.edit { preferences ->
        preferences[this] = value
    }
}

suspend fun Preferences.Key<Set<String>>.setSetString(value: Set<String>) {
    AppContext.get().dataStore.edit { preferences ->
        preferences[this] = value
    }
}

suspend fun Preferences.Key<ByteArray>.setByteArray(value: ByteArray) {
    AppContext.get().dataStore.edit { preferences ->
        preferences[this] = value
    }
}

