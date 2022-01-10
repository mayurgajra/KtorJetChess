package com.mayurg.data.models

data class PiecePosition(val x: Int, val y: Int) {
    operator fun plus(other: PiecePosition): PieceDelta {
        return PieceDelta(this.x + other.x, this.y + other.y)
    }

    operator fun minus(other: PiecePosition): PieceDelta {
        return PieceDelta(this.x - other.x, this.y - other.y)
    }


    operator fun plus(other: PieceDelta): PiecePosition {
        return PiecePosition(this.x + other.x, this.y + other.y)
    }
}