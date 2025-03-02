package com.example.danzle.startPage

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignInRequest{

    @FormUrlEncoded
    @POST("/login")
    fun signInRequest(
        // define input
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignInResponse> // define output

}
