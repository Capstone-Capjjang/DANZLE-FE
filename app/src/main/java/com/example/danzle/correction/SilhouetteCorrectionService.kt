package com.example.danzle.correction

import com.example.danzle.data.remote.response.auth.SilhouetteCorrectionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SilhouetteCorrectionService {
    @GET("/accuracy-session/video-paths")

    fun getCorrectionSilhouette(
        @Header("Authorization") token: String,
        @Query("songName") songName: String
    ): Call<SilhouetteCorrectionResponse>

}