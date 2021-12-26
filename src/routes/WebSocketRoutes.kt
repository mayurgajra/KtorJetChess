package com.mayurg.routes

import com.google.gson.JsonParser
import com.mayurg.data.models.*
import com.mayurg.data.requests.DisconnectRequest
import com.mayurg.gson
import com.mayurg.other.Constants.TYPE_JOIN_ROOM_HANDSHAKE
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
        standardWebSocket { socket, clientId, message, payload ->
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
                        payload.clientId
                    )
                    server.playerJoined(player)
                    if (!room.containsPlayer(player.clientId)) {
                        room.addPlayer(player.clientId, player.username, socket)
                    } else {
                        val playerInRoom = room.players.find { it.clientId == clientId }
                        playerInRoom?.socket = socket
                        playerInRoom?.startPinging()
                    }
                }

                is Ping -> {
                    server.players[clientId]?.receivedPong()
                }

                is DisconnectRequest -> {
                    server.playerLeft(clientId, true)
                }
            }
        }
    }
}

fun Route.standardWebSocket(
    handleFrame: suspend (
        socket: DefaultWebSocketServerSession,
        clientId: String,
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
                        else -> BaseModel::class.java
                    }
                    val payload = gson.fromJson(message, type)
                    handleFrame(this, session.clientId, message, payload)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Handle disconnects
            val playerWithClientId = server.getRoomWithClientId(session.clientId)?.players?.find {
                it.clientId == session.clientId
            }
            if (playerWithClientId != null) {
                server.playerLeft(session.clientId)
            }


        }
    }

}