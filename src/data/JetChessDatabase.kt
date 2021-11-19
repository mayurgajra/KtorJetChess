package com.mayurg.data

import com.mayurg.data.collections.Challenge
import com.mayurg.data.collections.User
import com.mayurg.data.responses.FEUser
import com.mayurg.security.checkHashForPassword
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
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

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return checkHashForPassword(passwordToCheck, actualPassword)
}

suspend fun getUsers(): List<FEUser> {
    return users.find().toList().map {
        FEUser(
            fullName = it.fullName,
            mobile = it.mobile,
            email = it.email,
            id = it.id,
        )
    }
}

suspend fun sendChallenge(challenge: Challenge): Boolean {
    return challenges.insertOne(challenge).wasAcknowledged()
}