package com.example.danzle.practice

import com.example.danzle.data.remote.response.auth.PracticeResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PracticeService {
    @POST("/practice-session/full")
    fun getFullPractice(
        @Query("songId") songId: Long,
        @Header("Authorization") token: String
    ): Call<List<PracticeResponse>>

    @POST("/practice-session/highlight")
    fun getHighlightPractice(
        @Query("songId") songId: Long,
        @Header("Authorization") token: String
    ): Call<List<PracticeResponse>>
}