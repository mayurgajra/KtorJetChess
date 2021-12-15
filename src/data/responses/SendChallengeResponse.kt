package com.mayurg.data.responses

data class SendChallengeResponse(
    val successful: Boolean,
    val message: String,
    val challengeId: String? = null
)
