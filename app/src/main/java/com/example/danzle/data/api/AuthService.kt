package com.example.danzle.data.api

import com.example.danzle.data.remote.request.auth.RefreshTokenRequest
import com.example.danzle.data.remote.response.auth.RefreshTokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/refresh")

    fun getRefreshToken(
        @Body refreshTokenRequest: RefreshTokenRequest
    ): Call<RefreshTokenResponse>
}