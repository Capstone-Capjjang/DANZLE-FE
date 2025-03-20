package com.example.danzle.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class MyProfileResponse (
    val id: Long,
    @SerializedName("name")
    val username: String,
    val email: String,
    val profileImageUrl: String,
    val practiceVideos: List<MyVideoResponse>,
    val challengeVideos: List<MyVideoResponse>,
    val accuracyHistory: List<AccuracySessionResponse>
)