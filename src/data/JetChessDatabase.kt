package com.mayurg.data

import com.mayurg.data.collections.Challenge
import com.mayurg.data.collections.User
import com.mayurg.data.requests.AcceptRejectChallengeRequest
import com.mayurg.data.responses.FEChallenge
import com.mayurg.data.responses.FEUser
import com.mayurg.security.checkHashForPassword
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("JetChessDatabase")
private val users = database.getCollection<User>()
private val challenges = database.getCollection<Challenge>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExists(email: String): Boolean {
    return users.findOne(User::email eq email) != null
}

suspend fun getUserByEmail(email: String): FEUser? {
    return users.findOne(User::email eq email)?.let {
        FEUser(
            fullName = it.fullName,
            mobile = it.mobile,
            email = it.email,
            id = it.id,
        )
    }
}

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return checkHashForPassword(passwordToCheck, actualPassword)
}

suspend fun getUsers(loggedInUserId: String): List<FEUser> {
    return users.find(User::id ne loggedInUserId).toList().map {
        FEUser(
            fullName = it.fullName,
            mobile = it.mobile,
            email = it.email,
            id = it.id,
        )
    }
}

suspend fun getChallenges(userId: String): List<FEChallenge> {
    return challenges.find(and(Challenge::toId eq userId, Challenge::status eq "none")).toList().map {
        val userName = users.findOne(User::id eq it.fromId)?.fullName.orEmpty()
        FEChallenge(
            id = it.id,
            fromId = it.fromId,
            toId = it.toId,
            fromUsername = userName
        )
    }
}

suspend fun sendChallenge(challenge: Challenge): String? {
    val isInsertSuccess = challenges.insertOne(challenge).wasAcknowledged()
    if (isInsertSuccess) {
        return challenge.id
    }
    return null
}

suspend fun setChallengeStatus(request: AcceptRejectChallengeRequest): Boolean {
    return challenges.updateOneById(
        id = request.id,
        update = set(Challenge::status setTo request.status)
    ).wasAcknowledged()
}