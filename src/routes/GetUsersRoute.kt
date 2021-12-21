package com.mayurg.routes

import com.mayurg.data.getUsers
import com.mayurg.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getUsersRoute() {
    route("/getUsers") {
        get {
            try {

                val loggedInUserId = call.request.queryParameters["loggedInUserId"]

                if (loggedInUserId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val users = getUsers(loggedInUserId)
                call.respond(HttpStatusCode.OK, users)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "Request not well"))
            }
        }
    }
}