package com.example.danzle.startPage

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("access_token")
    val accessToken: String
)