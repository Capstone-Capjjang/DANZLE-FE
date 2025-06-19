package com.example.danzle.data.remote.response.auth

data class HighlightSessionResponse(
    val sessionId: Long,
    val user: UserInfo,
    val song: SongInfo,
    val startTime: Float,
    val endTime: Float,
    val duration: String
)

data class UserInfo(
    val username: String
)

data class SongInfo(
    val id: Long,
    val title: String
)