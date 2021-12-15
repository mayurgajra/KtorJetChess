package com.mayurg.routes

import com.mayurg.data.collections.Challenge
import com.mayurg.data.requests.SendChallengeRequest
import com.mayurg.data.responses.SendChallengeResponse
import com.mayurg.data.sendChallenge
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.sendChallenge() {
    route("/sendChallenge") {
        post {
            val request = try {
                call.receive<SendChallengeRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val challenge = Challenge(
                fromId = request.fromId,
                toId = request.toId
            )

            val challengeId = sendChallenge(challenge)
            if (challengeId != null) {
                call.respond(
                    HttpStatusCode.OK, SendChallengeResponse(
                        true,
                        "Challenge sent successfully",
                        challengeId
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    SendChallengeResponse(false, "Could not send challenge. Please try again.")
                )
            }
        }
    }
}