package com.example.danzle.data.remote.response.auth

data class PoseAnalysisResponse(
    val sessionId: Long,
    val song: Song,
    val score: Double,
    val feedback: String,
    val timestamp: String,
    val duration: String
)