package com.example.danzle.data.remote.response.auth

data class PoseAnalysisResponse(
    val sessionId: Long,
    val song: SongPractice,
    val score: Double,
    val resultTag: String,
    val timestamp: String,
    val duration: String
)