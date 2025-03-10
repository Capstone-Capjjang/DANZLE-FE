package com.example.danzle.startPage

import com.example.danzle.data.remote.request.auth.SignInRequest
import com.example.danzle.data.remote.response.auth.SignInResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SignInsService{

    @POST("/login")
    fun userLogin(
        @Body singInRequest: SignInRequest
    ): Call<SignInResponse>

}