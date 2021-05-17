package com.mayurg.routes

import com.mayurg.data.checkIfUserExists
import com.mayurg.data.collections.User
import com.mayurg.data.registerUser
import com.mayurg.data.requests.RegisterUserRequest
import com.mayurg.data.responses.SimpleResponse
import com.mayurg.security.getHashWithSalt
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute() {
    route("/register") {
        post {
            val request = try {
                call.receive<RegisterUserRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }

            val userExists = checkIfUserExists(request.email)
            if (!userExists) {
                val user = User(
                    fullName = request.fullName,
                    mobile = request.mobile,
                    email = request.email,
                    password = getHashWithSalt(request.password)
                )
                if (registerUser(user)) {
                    call.respond(OK, SimpleResponse(true, "Successfully created account!"))
                } else {
                    call.respond(OK, SimpleResponse(false, "An unknown error occurred"))
                }
            } else {
                call.respond(OK, SimpleResponse(false, "A user with that email already exists"))
            }
        }
    }
}