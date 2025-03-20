package com.example.danzle.myprofile

import com.example.danzle.data.remote.response.auth.MyProfileResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MyProfileSevice {
    @POST("/user/profile")

    fun getMyProfile(
        @Header("X-ACCESS-TOKEN") token: String
    ): Call<MyProfileResponse>
}