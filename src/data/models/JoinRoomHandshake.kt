package com.mayurg.data.models

import com.mayurg.other.Constants.TYPE_JOIN_ROOM_HANDSHAKE

data class JoinRoomHandshake(
    val username: String,
    val roomId: String,
    val clientId: String
) : BaseModel(TYPE_JOIN_ROOM_HANDSHAKE)
