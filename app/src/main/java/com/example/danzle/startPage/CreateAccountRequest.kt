package com.example.danzle.startPage

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CreateAccountRequest{

    @FormUrlEncoded
    @POST("/join")
    fun createAccountRequest(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password1") password1: String,
        @Field("password2") password2: String,
        @Field("termsAccepted") termsAccepted: Boolean
    ): Call<CreateAccountResponse>
}
