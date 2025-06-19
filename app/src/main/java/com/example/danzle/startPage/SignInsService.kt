package com.example.danzle.startPage

import com.example.danzle.data.remote.request.auth.SignInRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignInsService {
    @POST("/login")
    fun userLogin(
        @Body singInRequest: SignInRequest
    ): Call<ResponseBody>
}