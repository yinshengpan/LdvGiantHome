package com.ledvance.network.api

import com.ledvance.network.model.FirmwareInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/19/25 10:53
 * Describe : FirmwareApi
 */
internal interface FirmwareApi {

    @GET("https://www.ledvanceapp.com/ota-files/ledvance-giant-home/ota-config")
    suspend fun getFirmwareList(): List<FirmwareInfo>

    @GET
    @Streaming
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>
}