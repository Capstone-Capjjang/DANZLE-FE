package com.example.danzle.commonService

import com.example.danzle.data.remote.response.auth.SilhouetteResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SilhouetteService {
    @GET("/accuracy-session/video-paths")
    fun getSilhouette(
        @Header("Authorization") token: String,
        @Query("songName") songTitle: String
    ): Call<SilhouetteResponse>
}