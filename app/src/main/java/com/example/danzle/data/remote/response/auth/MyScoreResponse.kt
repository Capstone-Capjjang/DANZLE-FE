package com.example.danzle.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class MyScoreResponse (
    val id: Long,
    val user: User,
    val song: SongMyScore,
    val startTime: String,
    val endTime: String,
    @SerializedName("avg_score")
    val avgScore: Double,
    val mode: String,
    val feedbackCompleted: Boolean,
    val createdAt : String,
    val duration: String,
    val startTimeInSeconds: Long,
    val endTimeInSeconds: Long,
    @SerializedName("resultLevel")
    val feedback: String
)

data class User(
    @SerializedName("id")
    val userId: Long,
    val username: String,
    val password: String,
    val email: String,
    val role: String,
    val profileImageUrl: String
)

data class SongMyScore(
    @SerializedName("id")
    val songId: Long,
    val title: String,
    val artist: String,
    val duration: Int,
    val genre: String,
    val fullStartTime: Int,
    val fullEndTime: Int,
    val highlightStartTime: Int,
    val highlightEndTime: Int,
    val audioFilePath: String,
    val silhouetteVideoPath: String,
    val danceGuidePath: String,
    val avatarVideoWithAudioPath: String,
    val coverImagePath: String,
    val createBy: String
)