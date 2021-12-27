package com.mayurg

import com.mayurg.data.models.Player
import com.mayurg.data.models.Room
import java.util.concurrent.ConcurrentHashMap

class DrawingServer {

    val rooms = ConcurrentHashMap<String, Room>()
    val players = ConcurrentHashMap<String, Player>()

    fun playerJoined(player: Player) {
        players[player.playerId] = player
        player.startPinging()
    }

    fun playerLeft(playerId: String,immediatelyDisconnect:Boolean = false){
        val playersRoom = getRoomWithPlayerId(playerId)
        if (immediatelyDisconnect || players[playerId]?.isOnline == false){
            playersRoom?.removePlayer(playerId)
            players[playerId]?.disconnect()
            players.remove(playerId)
        }
    }

    fun getRoomWithPlayerId(playerId: String): Room? {
        val filteredRooms = rooms.filterValues { room ->
            room.players.find { player ->
                player.playerId == playerId
            } != null
        }

        return if (filteredRooms.values.isEmpty()) {
            null
        } else {
            filteredRooms.values.toList()[0]
        }
    }

}