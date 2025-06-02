package com.example.danzle.data.remote.response.auth

import com.example.danzle.enum.VideoMode

data class MyVideoResponse (
    val sessionId: Long,
    val songTitle: String,
    val artist: String,
    val mode: VideoMode,
    val songImgPath: String,
    val videoPath: String,
    val thumbnailUrl: String
)