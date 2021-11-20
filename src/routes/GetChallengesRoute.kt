package com.mayurg.routes

import com.mayurg.data.getChallenges
import com.mayurg.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getChallengesRoute() {
    route("/getChallenges") {
        get {
            try {
                val userId = call.request.queryParameters["userId"]

                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val challenges = getChallenges(userId)
                call.respond(HttpStatusCode.OK, challenges)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "Request not well"))
            }
        }
    }
}