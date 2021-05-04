package com.mayurg.data.requests

data class RegisterUserRequest(
    val fullName: String,
    val mobile: String,
    val email: String,
    val password: String
)