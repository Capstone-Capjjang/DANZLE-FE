package com.example.danzle.data.remote.response.auth


data class PracticeResponse(
    val sessionId: Long,
    val song: SongPractice,
    val mode: String,
    val startTime: String,
    val endTime: String,
    val timestamp: String,
    val duration: String
)

data class SongPractice(
    val id: Long,
    val title: String
)