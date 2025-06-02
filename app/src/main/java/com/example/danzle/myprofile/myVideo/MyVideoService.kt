package com.example.danzle.myprofile.myVideo

import com.example.danzle.data.remote.response.auth.MyVideoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MyVideoService {
    @GET("/recorded-video/user/me")
    fun getMyVideo(
        @Header("Authorization") token: String
    ): Call<List<MyVideoResponse>>
}