package com.example.danzle.correction

import com.example.danzle.data.remote.response.auth.CorrectionFeedbackDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CorrectionDetailFeedbackService {
    @GET("/api/low-score-feedback")
    fun getCorrectionDetailFeedback(
        @Header("Authorization") token: String,
        @Query("sessionId") sessionId: Long
    ): Call<List<CorrectionFeedbackDetailResponse>>
}