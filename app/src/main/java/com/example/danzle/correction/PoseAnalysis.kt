package com.example.danzle.correction

import com.example.danzle.data.remote.response.auth.PoseAnalysisResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PoseAnalysis {
    @Multipart
    @POST("accuracy-session/analyze")
    fun uploadFrame(
        @Header("Authorization") token: String,
        @Part frame: MultipartBody.Part,
        @Query("sec") sec: Int,
        @Query("songId") songId: Long,
        @Query("sessionId") sessionId: Long
    ): Call<PoseAnalysisResponse>
}