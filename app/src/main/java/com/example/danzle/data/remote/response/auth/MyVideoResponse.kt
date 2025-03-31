package com.example.danzle.data.remote.response.auth

import androidx.media3.common.C.VideoScalingMode

data class MyVideoResponse (
    val videoId: Long,
    val songTitle: String,
    val artist: String,
    val mode: VideoMode,
    val songImgPath: String,
    val videoPath: String
)

enum class VideoMode{
    PRACTICE,
    CHALLENGE
}