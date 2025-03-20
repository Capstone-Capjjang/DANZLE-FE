package com.example.danzle.data.remote.request.auth

import com.google.gson.annotations.SerializedName

data class ChangeUsernameRequest (
    @SerializedName("name")
    val username: String
)