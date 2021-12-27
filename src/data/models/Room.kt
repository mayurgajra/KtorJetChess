package com.mayurg.data.models

import io.ktor.websocket.*

class Room(
    val id: String,
    var players: List<Player> = listOf()
) {
    fun containsPlayer(playerId: String): Boolean {
        return players.find { it.playerId == playerId } != null
    }

    fun addPlayer(playerId: String, playerName: String, socket: DefaultWebSocketServerSession) {
        val player = Player(playerName, socket, playerId)
        players.toMutableList().add(player)
    }

    fun removePlayer(playerId: String) {
        val player = players.find { it.playerId == playerId }!!
        players = players - player
    }


}