package com.mayurg.routes

import com.mayurg.data.checkPasswordForEmail
import com.mayurg.data.getUserByEmail
import com.mayurg.data.requests.LoginUserRequest
import com.mayurg.data.responses.LoginResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute() {
    route("/login") {
        post {
            val request = try {
                call.receive<LoginUserRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val email = request.email.trim()
            val pass = request.password.trim()

            val isPasswordCorrect = checkPasswordForEmail(email, pass)
            if (isPasswordCorrect) {
                val user = getUserByEmail(email)
                call.respond(OK, LoginResponse(true, "You are now logged in", user))
            } else {
                call.respond(OK, LoginResponse(false, "The email or password is incorrect"))
            }
        }
    }
}