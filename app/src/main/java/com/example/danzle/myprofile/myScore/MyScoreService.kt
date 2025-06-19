package com.example.danzle.myprofile.myScore

import com.example.danzle.data.remote.response.auth.MyScoreResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MyScoreService {
    @GET("/accuracy-session/user/me")
    fun getMyScore(
        @Header("Authorization") token: String
    ): Call<List<MyScoreResponse>>
}