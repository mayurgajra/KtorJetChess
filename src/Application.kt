package com.mayurg

import com.google.gson.Gson
import com.mayurg.data.checkPasswordForEmail
import com.mayurg.routes.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import io.ktor.websocket.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val server = DrawingServer()
val gson = Gson()

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
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

