package com.example.danzle.startPage

import com.example.danzle.data.remote.request.auth.ForgotPassword1Request
import com.example.danzle.data.remote.response.auth.ForgotPassword1Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ForgotPassword1Service {
    @POST("/password-reset/request")
    fun changePassword(
        @Body forgotPassword1Request: ForgotPassword1Request
    ): Call<ForgotPassword1Response>
}