package com.example.danzle.myprofile.myVideo

import com.example.danzle.data.remote.response.auth.MyVideoResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface MyVideoService {
    @POST("/recorded-video/user/{userId}")

    fun getMyVideo(
        @Header("X-ACCESS-TOKEN") token: String,
        @retrofit2.http.Path("userId") userId: Long
    ): Call<List<MyVideoResponse>>
}