package com.example.danzle.challenge

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ChallengeSaveService {
    @POST("/challenge-session/save")
    fun saveChallengeSession(
        @Header("Authorization") authHeader: String,
        @Query("sessionId") sessionId: Long
    ): Call<Any>
}