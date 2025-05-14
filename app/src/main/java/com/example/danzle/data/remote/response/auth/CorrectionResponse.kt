package com.example.danzle.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class CorrectionResponse(
    val sessionId: Long,
    val song: Song,
    val score: Double,
    val feedback: String,
    val timestamp: String,
    val duration: String
)