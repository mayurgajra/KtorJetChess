package com.mayurg.routes

import com.mayurg.data.requests.AcceptRejectChallengeRequest
import com.mayurg.data.responses.SimpleResponse
import com.mayurg.data.setChallengeStatus
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.acceptRejectChallenge() {
    route("/acceptRejectChallenge") {
        post {
            val request = try {
                call.receive<AcceptRejectChallengeRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }

            val challengeRequest = AcceptRejectChallengeRequest(
                id = request.id,
                status = request.status
            )

            if (setChallengeStatus(challengeRequest)) {
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Challenge ${challengeRequest.status}"))
            } else {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "Something went wrong. Please try again."))
            }
        }
    }
}