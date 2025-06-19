package com.example.danzle.data.remote.response.auth

data class CorrectionResultResponse(
    val sessionId: Long,
    val song: Song,
    val score: Double,
    val timeStamp: String,
    val perfect: Int,
    val good: Int,
    val normal: Int,
    val bad: Int,
    val miss: Int,
    val resultLevel: String
)

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val coverImagePath: String,
    val createdBy: String
)