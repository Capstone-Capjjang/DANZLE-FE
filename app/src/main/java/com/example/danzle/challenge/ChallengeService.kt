package com.example.danzle.challenge

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ChallengeService {
    @GET("challenge-session/background/{songId}")
    fun getChallenge(
        @Header("Authorization") authHeader: String,
        @Path("songId") songId: Long
    ): Call<List<String>>
}