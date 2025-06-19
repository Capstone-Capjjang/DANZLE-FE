package com.example.danzle.challenge

import com.example.danzle.data.remote.response.auth.HighlightSessionResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ChallengeHighlightService {
    @POST("challenge-session/highlight")
    suspend fun getChallengeHighlightSession(
        @Header("Authorization") authHeader: String,
        @Query("songId") songId: Long
    ): Response<List<HighlightSessionResponse>>
}