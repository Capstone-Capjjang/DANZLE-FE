package com.example.danzle.myprofile.myVideo

import com.example.danzle.data.remote.response.auth.MyVideoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface PracticeVideoRepositoryService {
    @GET("/recorded-video/user/me?mode=PRACTICE")
    fun getPracticeVideo(
        @Header("Authorization") authHeader: String
    ): Call<List<MyVideoResponse>>
}
