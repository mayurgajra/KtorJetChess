package com.mayurg.data.responses

data class LoginResponse(
    val successful: Boolean,
    val message: String,
    val user: FEUser? = null
)