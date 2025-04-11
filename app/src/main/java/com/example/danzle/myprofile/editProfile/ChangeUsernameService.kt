package com.example.danzle.myprofile.editProfile

import com.example.danzle.data.remote.request.auth.ChangeUsernameRequest
import com.example.danzle.data.remote.response.auth.ChangeUsernameResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChangeUsernameService {
    @POST("/user/profile/edit/name")

    fun getChangeUsername(
        @Header("Authorization") token: String,
        @Body changeUsernameRequest: ChangeUsernameRequest
    ): Call<ChangeUsernameResponse>

}