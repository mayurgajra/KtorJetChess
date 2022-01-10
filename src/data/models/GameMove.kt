package com.mayurg.data.models

import com.mayurg.other.Constants.TYPE_MOVE

data class GameMove(
    val userId: String,
    val roomId: String,
    val move: Move,
): BaseModel(TYPE_MOVE)