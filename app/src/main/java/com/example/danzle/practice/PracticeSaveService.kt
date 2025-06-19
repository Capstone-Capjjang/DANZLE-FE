package com.example.danzle.practice

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PracticeSaveService {
    @POST("/practice-session/save")
    fun savePracticeSession(
        @Header("Authorization") authHeader: String,
        @Query("sessionId") sessionId: Long
    ): Call<Any>
}