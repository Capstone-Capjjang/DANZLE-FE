package com.example.danzle.correction

import com.example.danzle.data.remote.response.auth.MusicSelectResponse
import retrofit2.Call
import retrofit2.http.GET

interface CorrectionMusicSelectService {
    @GET("/song/all")
    fun getCorrectionMusicSelect(): Call<List<MusicSelectResponse>>
}