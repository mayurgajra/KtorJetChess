package com.mayurg.data.models

import com.mayurg.gson
import com.mayurg.other.Constants.PING_FREQUENCY
import com.mayurg.server
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Player(
    val playerName: String,
    var socket: WebSocketSession,
    val playerId: String,
    var isMakingMove: Boolean = false,
) {
    private var pingJob: Job? = null

    private var pingTime = 0L
    private var pongTime = 0L

    var isOnline = true

    fun startPinging() {
        pingJob?.cancel()
        pingJob = GlobalScope.launch {
            while (true) {
                sendPing()
                delay(PING_FREQUENCY)
            }
        }
    }

    private suspend fun sendPing() {
        pingTime = System.currentTimeMillis()
        socket.send(Frame.Text(gson.toJson(Ping())))
        delay(PING_FREQUENCY)
        if (pingTime - pongTime > PING_FREQUENCY){
            isOnline = false
            server.playerLeft(playerId)
            pingJob?.cancel()
        }
    }

    fun receivedPong(){
        pongTime = System.currentTimeMillis()
        isOnline = true
    }

    fun disconnect(){
        pingJob?.cancel()
    }
}
