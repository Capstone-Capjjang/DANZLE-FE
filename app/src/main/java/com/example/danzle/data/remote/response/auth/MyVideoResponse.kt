package com.example.danzle.data.remote.response.auth

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