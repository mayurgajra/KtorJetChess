package com.mayurg.data.models

import com.mayurg.other.Constants.TYPE_MOVE
import java.io.Serializable


data class Move(val fromPosition: PiecePosition, val toPosition: PiecePosition) : Serializable {
    fun contains(position: PiecePosition): Boolean {
        return fromPosition == position || toPosition == position
    }
}
