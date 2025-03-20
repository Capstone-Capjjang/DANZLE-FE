package com.example.danzle.data.remote.request.auth

import com.example.danzle.startPage.FirstStart
import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    val currentPassword: String,
    @SerializedName("newPassword1")
    val newPassword: String,
    @SerializedName("newPassword2")
    val confirmNewPassword: String
)
