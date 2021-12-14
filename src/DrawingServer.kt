package com.mayurg

import com.mayurg.data.models.Player
import com.mayurg.data.models.Room
import java.util.concurrent.ConcurrentHashMap

class DrawingServer {

    val rooms = ConcurrentHashMap<String, Room>()
    val players = ConcurrentHashMap<String, Player>()

    fun playerJoined(player: Player) {
        players[player.clientId] = player
        player.startPinging()
    }

    fun playerLeft(clientId: String,immediatelyDisconnect:Boolean = false){
        val playersRoom = getRoomWithClientId(clientId)
        if (immediatelyDisconnect || players[clientId]?.isOnline == false){
//            playersRoom?.removePlayer(clientId)
            players[clientId]?.disconnect()
            players.remove(clientId)
        }
    }

    fun getRoomWithClientId(clientId: String): Room? {
        val filteredRooms = rooms.filterValues { room ->
            room.players.find { player ->
                player.clientId == clientId
            } != null
        }

        return if (filteredRooms.values.isEmpty()) {
            null
        } else {
            filteredRooms.values.toList()[0]
        }
    }

}