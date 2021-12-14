package com.mayurg.routes

import com.mayurg.data.models.Room
import com.mayurg.data.requests.CreateGameRoomRequest
import com.mayurg.data.responses.SimpleResponse
import com.mayurg.server
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createGameRoomRoute() {
    route("/createGameRoom") {
        post {
            val roomRequest = call.receiveOrNull<CreateGameRoomRequest>()
            if (roomRequest == null) {
                call.respond(BadRequest)
                return@post
            }
            if (server.rooms[roomRequest.roomId] != null) {
                call.respond(OK, SimpleResponse(false, "Room already exists."))
                return@post
            }

            val room = Room(roomRequest.roomId)

            server.rooms[roomRequest.roomId] = room
            println("Room created: ${roomRequest.roomId}")
            call.respond(OK, SimpleResponse(true, "Room created"))

        }
    }
}

fun Route.joinRoomRoute() {
    route("/joinGameRoom") {
        get {
            val username = call.parameters["userId"]
            val roomName = call.parameters["roomId"]
            if (username == null || roomName == null) {
                call.respond(BadRequest)
                return@get
            }
            val room = server.rooms[roomName]
            when {
                room == null -> {
                    call.respond(OK, SimpleResponse(false, "Room not found"))
                }
                else -> {
                    call.respond(OK,SimpleResponse(true,"Room joined"))
                }
            }
        }
    }
}