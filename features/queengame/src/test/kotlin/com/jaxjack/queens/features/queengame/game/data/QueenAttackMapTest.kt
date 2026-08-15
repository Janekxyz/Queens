package com.jaxjack.queens.features.queengame.game.data

import com.jaxjack.queens.board.BoardPosition
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class QueenAttackMapTest {

    @Test
    fun `an empty board has no attacked squares`() {
        val attackMap = QueenAttackMap.of(size = 4, queens = emptySet())

        forEachSquare(size = 4) { x, y ->
            assertFalse(attackMap.isAttacked(x, y), "($x, $y) should not be attacked")
        }
    }

    @Test
    fun `an empty board has no conflicts`() {
        val attackMap = QueenAttackMap.of(size = 4, queens = emptySet())

        forEachSquare(size = 4) { x, y ->
            assertFalse(attackMap.isConflicted(x, y), "($x, $y) should not be conflicted")
        }
    }

    @Test
    fun `queen attacks its own square`() {
        val attackMap = attackMapWith(BoardPosition(x = 1, y = 2))

        assertTrue(attackMap.isAttacked(x = 1, y = 2))
    }

    @Test
    fun `queen attacks its row`() {
        val attackMap = attackMapWith(BoardPosition(x = 1, y = 2))

        assertTrue(attackMap.isAttacked(x = 0, y = 2))
        assertTrue(attackMap.isAttacked(x = 3, y = 2))
    }

    @Test
    fun `queen attacks its column`() {
        val attackMap = attackMapWith(BoardPosition(x = 1, y = 2))

        assertTrue(attackMap.isAttacked(x = 1, y = 0))
        assertTrue(attackMap.isAttacked(x = 1, y = 3))
    }

    @Test
    fun `queen attacks its diagonal`() {
        val attackMap = attackMapWith(BoardPosition(x = 1, y = 1))

        assertTrue(attackMap.isAttacked(x = 0, y = 0))
        assertTrue(attackMap.isAttacked(x = 2, y = 2))
        assertTrue(attackMap.isAttacked(x = 3, y = 3))
    }

    @Test
    fun `queen attacks its anti diagonal`() {
        val attackMap = attackMapWith(BoardPosition(x = 1, y = 2))

        assertTrue(attackMap.isAttacked(x = 0, y = 3))
        assertTrue(attackMap.isAttacked(x = 2, y = 1))
        assertTrue(attackMap.isAttacked(x = 3, y = 0))
    }

    @Test
    fun `queen does not attack a square off all of its lines`() {
        val attackMap = attackMapWith(BoardPosition(x = 0, y = 0))

        assertFalse(attackMap.isAttacked(x = 1, y = 2))
        assertFalse(attackMap.isAttacked(x = 2, y = 1))
        assertFalse(attackMap.isAttacked(x = 1, y = 3))
        assertFalse(attackMap.isAttacked(x = 3, y = 1))
    }

    @Test
    fun `a single queen creates no conflict`() {
        val attackMap = attackMapWith(BoardPosition(x = 1, y = 2))

        forEachSquare(size = 4) { x, y ->
            assertFalse(attackMap.isConflicted(x, y), "($x, $y) should not be conflicted")
        }
    }

    @Test
    fun `two queens on the same row conflict`() {
        val attackMap = attackMapWith(BoardPosition(x = 0, y = 1), BoardPosition(x = 3, y = 1))

        assertTrue(attackMap.isConflicted(x = 0, y = 1))
        assertTrue(attackMap.isConflicted(x = 3, y = 1))
    }

    @Test
    fun `two queens on the same column conflict`() {
        val attackMap = attackMapWith(BoardPosition(x = 2, y = 0), BoardPosition(x = 2, y = 3))

        assertTrue(attackMap.isConflicted(x = 2, y = 0))
        assertTrue(attackMap.isConflicted(x = 2, y = 3))
    }

    @Test
    fun `two queens on the same diagonal conflict`() {
        val attackMap = attackMapWith(BoardPosition(x = 1, y = 1), BoardPosition(x = 3, y = 3))

        assertTrue(attackMap.isConflicted(x = 1, y = 1))
        assertTrue(attackMap.isConflicted(x = 3, y = 3))
    }

    @Test
    fun `two queens on the same anti diagonal conflict`() {
        val attackMap = attackMapWith(BoardPosition(x = 1, y = 2), BoardPosition(x = 2, y = 1))

        assertTrue(attackMap.isConflicted(x = 1, y = 2))
        assertTrue(attackMap.isConflicted(x = 2, y = 1))
    }

    @Test
    fun `queens a knight move apart do not conflict`() {
        val attackMap = attackMapWith(BoardPosition(x = 0, y = 0), BoardPosition(x = 1, y = 2))

        assertFalse(attackMap.isConflicted(x = 0, y = 0))
        assertFalse(attackMap.isConflicted(x = 1, y = 2))
    }

    @Test
    fun `queens on the corners of the main diagonal conflict`() {
        val attackMap = attackMapWith(BoardPosition(x = 0, y = 0), BoardPosition(x = 3, y = 3))

        assertTrue(attackMap.isConflicted(x = 0, y = 0))
        assertTrue(attackMap.isConflicted(x = 3, y = 3))
    }

    @Test
    fun `queens on the corners of the anti diagonal conflict`() {
        val attackMap = attackMapWith(BoardPosition(x = 0, y = 3), BoardPosition(x = 3, y = 0))

        assertTrue(attackMap.isConflicted(x = 0, y = 3))
        assertTrue(attackMap.isConflicted(x = 3, y = 0))
    }

    @Test
    fun `a solved board has no conflicts`() {
        val attackMap = QueenAttackMap.of(size = 4, queens = FOUR_QUEENS_SOLUTION)

        FOUR_QUEENS_SOLUTION.forEach { queen ->
            assertFalse(
                attackMap.isConflicted(queen.x, queen.y),
                "(${queen.x}, ${queen.y}) should not be conflicted"
            )
        }
    }

    @Test
    fun `a solved board attacks every square`() {
        val attackMap = QueenAttackMap.of(size = 4, queens = FOUR_QUEENS_SOLUTION)

        forEachSquare(size = 4) { x, y ->
            assertTrue(attackMap.isAttacked(x, y), "($x, $y) should be attacked")
        }
    }

    @Test
    fun `every square of a doubled line reports a conflict`() {
        val attackMap = attackMapWith(BoardPosition(x = 0, y = 1), BoardPosition(x = 3, y = 1))

        assertTrue(attackMap.isConflicted(x = 1, y = 1))
        assertTrue(attackMap.isConflicted(x = 2, y = 1))
    }

    @Test
    fun `diagonals stay independent on a larger board`() {
        val attackMap = QueenAttackMap.of(
            size = 8,
            queens = setOf(BoardPosition(x = 0, y = 0), BoardPosition(x = 7, y = 7)),
        )

        assertTrue(attackMap.isConflicted(x = 0, y = 0))
        assertTrue(attackMap.isConflicted(x = 7, y = 7))
        assertFalse(attackMap.isConflicted(x = 7, y = 0))
        assertFalse(attackMap.isConflicted(x = 0, y = 7))
    }

    @Test
    fun `every square of a larger board can be queried`() {
        val attackMap = QueenAttackMap.of(
            size = 8,
            queens = setOf(BoardPosition(x = 0, y = 7), BoardPosition(x = 7, y = 0)),
        )

        forEachSquare(size = 8) { x, y ->
            attackMap.isAttacked(x, y)
            attackMap.isConflicted(x, y)
        }
    }

    private fun attackMapWith(vararg queens: BoardPosition) =
        QueenAttackMap.of(size = 4, queens = queens.toSet())

    private fun forEachSquare(size: Int, assertion: (x: Int, y: Int) -> Unit) {
        for (x in 0 until size) {
            for (y in 0 until size) {
                assertion(x, y)
            }
        }
    }
}

private val FOUR_QUEENS_SOLUTION = setOf(
    BoardPosition(x = 1, y = 0),
    BoardPosition(x = 3, y = 1),
    BoardPosition(x = 0, y = 2),
    BoardPosition(x = 2, y = 3),
)
