package com.jaxjack.queens.features.queengame.game.data

import com.jaxjack.queens.board.BoardPosition

class QueenAttackMap private constructor(
    private val size: Int,
    private val rows: IntArray,
    private val cols: IntArray,
    private val diagonals: IntArray,
    private val antiDiagonals: IntArray,
) {
    fun isAttacked(x: Int, y: Int): Boolean =
        rows[y] > 0 ||
                cols[x] > 0 ||
                diagonals[diagonalIndex(x, y)] > 0 ||
                antiDiagonals[x + y] > 0

    fun isConflicted(x: Int, y: Int): Boolean =
        rows[y] > 1 ||
                cols[x] > 1 ||
                diagonals[diagonalIndex(x, y)] > 1 ||
                antiDiagonals[x + y] > 1

    private fun diagonalIndex(x: Int, y: Int) = x - y + size - 1

    companion object {
        fun of(size: Int, queens: Set<BoardPosition>): QueenAttackMap {
            val rows = IntArray(size)
            val cols = IntArray(size)
            val diagonals = IntArray(2 * size - 1)
            val antiDiagonals = IntArray(2 * size - 1)

            for (queen in queens) {
                rows[queen.y]++
                cols[queen.x]++
                diagonals[queen.x - queen.y + size - 1]++
                antiDiagonals[queen.x + queen.y]++
            }

            return QueenAttackMap(size, rows, cols, diagonals, antiDiagonals)
        }
    }
}