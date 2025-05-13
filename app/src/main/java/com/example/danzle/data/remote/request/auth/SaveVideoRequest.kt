package com.example.danzle.data.remote.request.auth

import com.example.danzle.data.remote.response.auth.VideoMode

data class SaveVideoRequest(
    val sessionId: Long,
    val videoMode: VideoMode,
    val recordedAt: String,
    val duration: Int
)