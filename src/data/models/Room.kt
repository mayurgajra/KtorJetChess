package com.mayurg.data.models

import io.ktor.websocket.*

class Room(
    val id: String,
    var players: List<Player> = listOf()
) {
    fun containsPlayer(clientId: String): Boolean {
        return true
    }

    fun addPlayer(clientId: String, username: String, socket: DefaultWebSocketServerSession) {

    }


}