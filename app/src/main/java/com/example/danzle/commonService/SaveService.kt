package com.example.danzle.commonService

import com.example.danzle.data.remote.response.auth.SaveResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SaveService {
    @POST("/accuracy-session/save")
    fun saveSession(
        @Header("Authorization")token: String,
        @Query("sessionId") sessionId: Long
    ): Call<SaveResponse>
}