package com.mayurg.data.responses

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class FEChallenge(
    @BsonId
    val id: String = ObjectId().toString(),
    val fromId: String,
    val fromUsername: String,
    val toId: String,
)