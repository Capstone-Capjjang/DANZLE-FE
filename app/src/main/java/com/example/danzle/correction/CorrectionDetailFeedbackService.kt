package com.example.danzle.correction

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CorrectionDetailFeedbackService {
    @GET("/api/low-score-feedback")
    fun getCorrectionDetailFeedback(
        @Query("sessionId") sessionId: Long
    ): Call<List<String>>
}