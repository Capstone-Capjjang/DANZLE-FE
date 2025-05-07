package com.example.danzle.practice

import com.example.danzle.data.remote.response.auth.PracticeMusicSelectResponse
import retrofit2.Call
import retrofit2.http.GET

interface PracticeMusicSelectService {
    @GET("/song/all")
    fun getPracticeMusicSelect(): Call<List<PracticeMusicSelectResponse>>
}