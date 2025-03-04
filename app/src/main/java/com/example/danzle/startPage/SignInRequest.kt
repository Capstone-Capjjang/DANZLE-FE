package com.example.danzle.startPage

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.Headers
import retrofit2.http.POST

interface SignInRequest{

    @Headers("Content-Type:application/json")
    @POST("/login")
    fun signInRequest(
        // define input
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignInResponse> // define output

}
