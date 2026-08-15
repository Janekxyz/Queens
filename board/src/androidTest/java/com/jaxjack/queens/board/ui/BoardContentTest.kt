package com.jaxjack.queens.board.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jaxjack.queens.board.Board
import com.jaxjack.queens.board.BoardPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoardContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `renders a cell for every square of the board`() {
        setBoard(size = 4)

        forEachSquare(size = 4) { position ->
            composeRule.onNodeWithTag(boardCellTestTag(position)).assertExists()
        }
    }

    @Test
    fun `renders a cell for every square of a larger board`() {
        setBoard(size = 8)

        forEachSquare(size = 8) { position ->
            composeRule.onNodeWithTag(boardCellTestTag(position)).assertExists()
        }
    }

    @Test
    fun `renders the provided content inside every cell`() {
        composeRule.setContent {
            BoardContent(
                params = BoardParams(board = Board.create(size = 4)),
                cell = { position -> Text(text = "${position.x}${position.y}") }
            )
        }

        forEachSquare(size = 4) { position ->
            composeRule.onNodeWithText("${position.x}${position.y}").assertExists()
        }
    }

    @Test
    fun `lays out cells in a grid`() {
        setBoard(size = 4)

        val origin = cellBounds(BoardPosition(x = 0, y = 0))
        val right = cellBounds(BoardPosition(x = 1, y = 0))
        val below = cellBounds(BoardPosition(x = 0, y = 1))

        assertTrue("cell to the right should start further right", right.left > origin.left)
        assertTrue("cell to the right should share the top edge", right.top == origin.top)
        assertTrue("cell below should start lower", below.top > origin.top)
        assertTrue("cell below should share the left edge", below.left == origin.left)
    }

    @Test
    fun `cells are square and share the same size`() {
        setBoard(size = 4)

        val origin = cellBounds(BoardPosition(x = 0, y = 0))

        composeRule.onNodeWithTag(boardCellTestTag(BoardPosition(x = 0, y = 0)))
            .assertWidthIsEqualTo(origin.height)

        composeRule.onNodeWithTag(boardCellTestTag(BoardPosition(x = 3, y = 3)))
            .assertWidthIsEqualTo(origin.width)
            .assertHeightIsEqualTo(origin.height)
    }

    @Test
    fun `cells divide the board evenly`() {
        setBoard(size = 8)

        val first = cellBounds(BoardPosition(x = 0, y = 0))
        val second = cellBounds(BoardPosition(x = 1, y = 0))
        val last = cellBounds(BoardPosition(x = 7, y = 0))

        val step = second.left - first.left
        assertEquals(first.width.value, step.value, TOLERANCE)
        assertEquals((first.left + step * 7).value, last.left.value, TOLERANCE)
    }

    private fun setBoard(size: Int) {
        composeRule.setContent {
            BoardContent(params = BoardParams(board = Board.create(size = size)))
        }
    }

    private fun cellBounds(position: BoardPosition) =
        composeRule.onNodeWithTag(boardCellTestTag(position)).getUnclippedBoundsInRoot()

    private fun forEachSquare(size: Int, assertion: (BoardPosition) -> Unit) {
        for (x in 0 until size) {
            for (y in 0 until size) {
                assertion(BoardPosition(x = x, y = y))
            }
        }
    }
}

private const val TOLERANCE = 0.5f
