package com.example.danzle.myprofile

import com.example.danzle.data.remote.response.auth.MyProfileResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MyProfileService {
    @GET("/user/profile")

    fun getMyProfile(
        @Header("Authorization") token: String
    ): Call<MyProfileResponse>
}