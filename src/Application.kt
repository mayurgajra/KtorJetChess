package com.mayurg

import com.mayurg.data.checkPasswordForEmail
import com.mayurg.routes.getUsersRoute
import com.mayurg.routes.loginRoute
import com.mayurg.routes.registerRoute
import com.mayurg.routes.sendChallenge
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        registerRoute()
        loginRoute()
        getUsersRoute()
        sendChallenge()
    }
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Authentication) {
        configureAuth()
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

