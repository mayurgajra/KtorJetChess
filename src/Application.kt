package com.mayurg

import com.google.gson.Gson
import com.mayurg.data.checkPasswordForEmail
import com.mayurg.routes.*
import com.mayurg.session.GameSession
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.websocket.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val server = DrawingServer()
val gson = Gson()

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(Sessions) {
        cookie<GameSession>("SESSION")
    }
    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<GameSession>() == null) {
            val playerId = call.parameters["client_id"] ?: ""
            call.sessions.set(GameSession(playerId, generateNonce()))
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Authentication) {
        configureAuth()
    }

    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets)
    install(Routing) {
        registerRoute()
        loginRoute()
        getUsersRoute()
        sendChallenge()
        getChallengesRoute()
        acceptRejectChallenge()
        createGameRoomRoute()
        joinRoomRoute()
        gameWebSocketRoute()
    }
}

private fun Authentication.Configuration.configureAuth() {
    basic {
        realm = "Jet Chess Server"
        validate { credentials ->
            val email = credentials.name
            val password = credentials.password
            if (checkPasswordForEmail(email, password)) {
                UserIdPrincipal(email)
            } else null
        }
    }
}

