package com.mayurg.routes

import com.google.gson.JsonParser
import com.mayurg.data.models.*
import com.mayurg.data.requests.DisconnectRequest
import com.mayurg.gson
import com.mayurg.other.Constants.TYPE_JOIN_ROOM_HANDSHAKE
import com.mayurg.other.Constants.TYPE_MOVE
import com.mayurg.other.Constants.TYPE_PING
import com.mayurg.server
import com.mayurg.session.GameSession
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach


fun Route.gameWebSocketRoute() {
    route("/ws/playGame") {
        standardWebSocket { socket, playerId, message, payload ->
            when (payload) {
                is JoinRoomHandshake -> {
                    val room = server.rooms[payload.roomId]
                    if (room == null) {
                        val gameError = GameError(GameError.ERROR_ROOM_NOT_FOUND)
                        socket.send(Frame.Text(gson.toJson(gameError)))
                        return@standardWebSocket
                    }

                    val player = Player(
                        payload.username,
                        socket,
                        payload.playerId
                    )
                    server.playerJoined(player)
                    if (!room.containsPlayer(player.playerId)) {
                        room.addPlayer(player.playerId, player.playerName, socket)
                    } else {
                        val playerInRoom = room.players.find { it.playerId == playerId }
                        playerInRoom?.socket = socket
                        playerInRoom?.startPinging()
                    }

                    println("JoinRoom called")
                }


                is GameMove -> {
                    val room = server.rooms[payload.roomId]
                    if (room == null) {
                        val gameError = GameError(GameError.ERROR_ROOM_NOT_FOUND)
                        socket.send(Frame.Text(gson.toJson(gameError)))
                        return@standardWebSocket
                    }

                    room.broadcastToAllExcept(gson.toJson(payload), payload.userId)
                }


                is Ping -> {
                    server.players[playerId]?.receivedPong()
                }

                is DisconnectRequest -> {
                    server.playerLeft(playerId, true)
                }
            }
        }
    }
}

fun Route.standardWebSocket(
    handleFrame: suspend (
        socket: DefaultWebSocketServerSession,
        playerId: String,
        message: String,
        payload: BaseModel
    ) -> Unit
) {
    webSocket {
        val session = call.sessions.get<GameSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }
        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val message = frame.readText()
                    val jsonObject = JsonParser.parseString(message).asJsonObject
                    val type = when (jsonObject.get("type").asString) {
                        TYPE_PING -> Ping::class.java
                        TYPE_JOIN_ROOM_HANDSHAKE -> JoinRoomHandshake::class.java
                        TYPE_MOVE -> GameMove::class.java
                        else -> BaseModel::class.java
                    }
                    val payload = gson.fromJson(message, type)
                    handleFrame(this, session.playerId, message, payload)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Handle disconnects
            val playerWithPlayerId = server.getRoomWithPlayerId(session.playerId)?.players?.find {
                it.playerId == session.playerId
            }
            if (playerWithPlayerId != null) {
                server.playerLeft(session.playerId)
            }


        }
    }

}