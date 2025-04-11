package com.example.danzle.myprofile.editProfile

import com.example.danzle.data.remote.request.auth.ChangePasswordRequest
import com.example.danzle.data.remote.response.auth.ChangePasswordResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChangePasswordService {
    @POST("/user/profile/edit/password")

    fun getChangePassword(
        @Header("Authorization") token: String,
        @Body changePasswordRequest: ChangePasswordRequest
    ): Call<ChangePasswordResponse>

}