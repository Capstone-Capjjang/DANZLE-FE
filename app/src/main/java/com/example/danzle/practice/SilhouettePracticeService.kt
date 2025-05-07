package com.example.danzle.practice

import com.example.danzle.data.remote.response.auth.SilhouettePracticeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SilhouettePracticeService {
    @GET("/practice-session/video-paths")

    fun getPracticeSilhouette(
        @Header("Authorization") token: String,
        @Query("songName") songName: String
    ): Call<SilhouettePracticeResponse>
}