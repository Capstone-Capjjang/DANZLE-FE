package com.example.danzle.practice

import com.example.danzle.data.remote.response.auth.HighlightPracticeResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface HighlightPracticeService {
    @POST("/practice-session/highlight")

    fun getHighlightPractice(
        @Query("songId") songId: Long,
        @Header("Authorization") token: String
    ): Call<List<HighlightPracticeResponse>>

}