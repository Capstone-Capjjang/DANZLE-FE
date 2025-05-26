package com.example.danzle.correction

import com.example.danzle.data.remote.response.auth.CorrectionResultResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CorrectionResultService {
    @GET("/accuracy-session/result")
    fun getCorrectionResultService(
        @Query("sessionId") sessionId: Long,
        @Header("authorization") token: String
    ): Call<CorrectionResultResponse>
}