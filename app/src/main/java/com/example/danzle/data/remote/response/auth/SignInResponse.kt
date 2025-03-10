package com.example.danzle.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    val grantType: String,
    @SerializedName("access_token")
    val accessToken: String,
    val refreshToken: String
)