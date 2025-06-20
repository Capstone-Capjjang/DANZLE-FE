package com.example.danzle.startPage

import com.example.danzle.data.remote.request.auth.CreateAccountRequest
import com.example.danzle.data.remote.response.auth.CreateAccountResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateAccountService {
    @POST("/join")
    fun userCreateAccount(
        @Body createAccountRequest: CreateAccountRequest
    ): Call<CreateAccountResponse>
}