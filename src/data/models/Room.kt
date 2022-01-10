package com.mayurg.data.models

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.isActive

class Room(
    val id: String,
    var players: List<Player> = listOf()
) {
    fun containsPlayer(playerId: String): Boolean {
        return players.find { it.playerId == playerId } != null
    }

    fun addPlayer(playerId: String, playerName: String, socket: DefaultWebSocketServerSession) {
        val player = Player(playerName, socket, playerId)
        val tmpPlayers = players.toMutableList()
        tmpPlayers.add(0, player)
        players = tmpPlayers
    }

    suspend fun broadcastToAllExcept(message: String, playerId: String) {
        players.forEach { player ->
            if (player.playerId != playerId && player.socket.isActive) {
                player.socket.send(Frame.Text(message))
            }
        }
    }

    fun removePlayer(playerId: String) {
        val player = players.find { it.playerId == playerId }!!
        players = players - player
    }


}