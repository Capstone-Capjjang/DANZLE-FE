package com.example.danzle.correction

import com.example.danzle.data.remote.response.auth.CorrectionResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MediapipeService {
    @Multipart
    @POST("/accuracy-session/analyze")

    fun getMediapipe(
        @Part("songId")songId: Long,
        @Part("videoPath")videoPath: String,
        @Part("sessionId")sessionId: Long,
        @Header("Authorization")token: String
    ): Call<List<CorrectionResponse>>

}