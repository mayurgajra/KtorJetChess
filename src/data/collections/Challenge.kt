package com.mayurg.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Challenge(
    @BsonId
    val id: String = ObjectId().toString(),
    val fromId: String,
    val toId: String,
)