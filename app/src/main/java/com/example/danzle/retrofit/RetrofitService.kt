package com.example.danzle.retrofit

import com.example.danzle.startPage.CreateAccountRequest
import com.example.danzle.startPage.SignInRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface RetrofitService {

    @POST("/join")
    fun createAccount(
        @Body request: CreateAccountRequest
    ): Call<UserToken>

    @POST("/login")
    fun signin(
        @Body request: SignInRequest
    ): Call<UserToken>
}