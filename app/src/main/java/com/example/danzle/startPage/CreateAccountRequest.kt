package com.example.danzle.startPage

data class CreateAccountRequest (
    val email: String,
    val username: String,
    val password1: String,
    val password2: String,
    val termsAccepted: Boolean
)
