package com.ledvance.network

import com.ledvance.network.api.FirmwareApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/19/25 10:46
 * Describe : NetworkModule
 */
internal object NetworkModule {
    private const val BASE_URL = "https://www.ledvanceapp.com/"
    private val json by lazy {
        Json {
            prettyPrint = false
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    private val logging by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.ledvanceapp.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .build()
    }

    val firmwareApi by lazy {
        retrofit.create(FirmwareApi::class.java)
    }
}