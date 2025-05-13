package com.example.danzle.data.remote.response.auth

data class SaveVideoResponse (
    val id : Int,
    val filePath: String,
    val mode: String,
    val songId: Int,
    val sessionId: Int,
    val duration: Int,
    val createdAt: String
)