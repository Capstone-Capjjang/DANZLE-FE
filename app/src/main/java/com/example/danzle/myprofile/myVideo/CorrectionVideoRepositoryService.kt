package com.example.danzle.myprofile.myVideo

import com.example.danzle.data.remote.response.auth.MyVideoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface CorrectionVideoRepositoryService {
    @GET("/recorded-video/user/me?mode=ACCURACY")
    fun getCorrectionVideo(
        @Header("Authorization") authHeader: String
    ): Call<List<MyVideoResponse>>
}