package com.example.danzle.correction

import com.example.danzle.data.remote.response.auth.CorrectionResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface CorrectionService {
    @FormUrlEncoded
    @POST("/accuracy-session/full")

    fun getCorrection(
        @Field("songId")songId: Long,
        @Header("Authorization")token: String
        ): Call<CorrectionResponse>
}