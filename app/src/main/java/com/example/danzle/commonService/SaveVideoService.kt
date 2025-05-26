package com.example.danzle.commonService

import com.example.danzle.data.remote.response.auth.SaveVideoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SaveVideoService {
    @Multipart
    @POST("/recorded-video/saveVideo")
    fun getSaveVideo(
        @Header("Authorization")token: String,
        @Part file: MultipartBody.Part,
        @Part("sessionId") sessionId: RequestBody,
        @Part("videoMode") videoMode: RequestBody,
        @Part("recordedAt") recordedAt: RequestBody,
        @Part("duration") duration: RequestBody
    ): Call<SaveVideoResponse>
}