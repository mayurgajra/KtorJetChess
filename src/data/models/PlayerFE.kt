package com.mayurg.data.models

import com.mayurg.other.Constants.TYPE_PLAYER_FE

data class PlayerFE(
    val playerName: String = "",
    val playerId: String = ""
) : BaseModel(TYPE_PLAYER_FE)