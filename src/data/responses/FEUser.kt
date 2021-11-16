package com.mayurg.data.responses

import org.bson.types.ObjectId

data class FEUser(
    val fullName: String? = null,
    val mobile: String? = null,
    val email: String,
    val id: String = ObjectId().toString()
)