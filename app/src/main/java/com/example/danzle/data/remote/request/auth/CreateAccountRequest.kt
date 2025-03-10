package com.example.danzle.data.remote.request.auth

data class CreateAccountRequest(
    val username: String,
    val email: String,
    val password1: String,
    val password2: String,
    val termsAccepted: Boolean
)
