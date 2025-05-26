package com.example.danzle.correction

import com.example.danzle.data.remote.response.auth.FakeFeedbackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FakeFeedbackService {
    @GET("/api/fake-feedback")
    fun getFakeFeedback(
        @Header("Authorization")token: String,
        @Query("sessionId") sessionId: Long
    ): Call<List<FakeFeedbackResponse>>
}