package com.example.danzle.data.remote.response.auth

data class CorrectionFeedbackDetailResponse (
    val frameIndex: Int,
    val feedbacks: List<String>,
    val userImageUrl: String,
    val expertImageUrl: String
)