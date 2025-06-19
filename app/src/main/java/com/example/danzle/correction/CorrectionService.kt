package com.example.danzle.correction

import com.example.danzle.data.remote.response.auth.CorrectionResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CorrectionService {
    @POST("/accuracy-session/full")
    fun getCorrection(
        @Header("Authorization") token: String,
        @Query("songId") songId: Long,
    ): Call<CorrectionResponse>
}