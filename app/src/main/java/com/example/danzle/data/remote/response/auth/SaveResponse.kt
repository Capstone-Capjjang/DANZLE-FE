package com.example.danzle.data.remote.response.auth

data class SaveResponse(
    val sessionId: Long,
    val song: SongPractice,
    val score: Double,
    val feedback: String,
    val timestamp: String,
    val duration: String
)