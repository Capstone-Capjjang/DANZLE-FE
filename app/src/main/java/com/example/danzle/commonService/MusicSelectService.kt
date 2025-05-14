package com.example.danzle.commonService

import com.example.danzle.data.remote.response.auth.MusicSelectResponse
import retrofit2.Call
import retrofit2.http.GET

interface MusicSelectService {
    @GET("/song/all")
    fun getMusicSelect(): Call<List<MusicSelectResponse>>
}