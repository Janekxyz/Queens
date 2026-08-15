package com.jaxjack.queens.features.queengame.game

import app.cash.turbine.test
import com.jaxjack.queens.board.BoardPosition
import com.jaxjack.queens.core.testing.FakeGameResultRepository
import com.jaxjack.queens.core.testing.FakeTimeProvider
import com.jaxjack.queens.core.testing.TestCoroutineExtension
import com.jaxjack.queens.features.queengame.QueenColor
import com.jaxjack.queens.features.queengame.game.navigation.QueenGameKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TestCoroutineExtension::class)
class QueenGameViewModelTest {

    private val repository = FakeGameResultRepository()
    private val timeProvider = FakeTimeProvider()

    @Test
    fun `starts with an empty board of the requested size and color`() = runTest {
        val viewModel = createViewModel(boardSize = 6, queenColor = QueenColor.Black)

        viewModel.viewState.test {
            val state = awaitItem()

            assertEquals(6, state.board.size)
            assertEquals(emptySet<BoardPosition>(), state.queens)
            assertEquals(QueenColor.Black, state.queenColor)
            assertFalse(state.isSolved)
        }
    }

    @Test
    fun `tapping an empty tile places a queen`() = runTest {
        val viewModel = createViewModel()

        viewModel.viewState.test {
            assertEquals(emptySet<BoardPosition>(), awaitItem().queens)

            viewModel.onAction(QueenGameAction.TileClick(BoardPosition(x = 0, y = 0)))

            assertEquals(setOf(BoardPosition(x = 0, y = 0)), awaitItem().queens)
        }
    }

    @Test
    fun `tapping a queen removes it`() = runTest {
        val viewModel = createViewModel()
        val position = BoardPosition(x = 2, y = 1)
        viewModel.onAction(QueenGameAction.TileClick(position))

        viewModel.viewState.test {
            assertEquals(setOf(position), awaitItem().queens)

            viewModel.onAction(QueenGameAction.TileClick(position))

            assertEquals(emptySet<BoardPosition>(), awaitItem().queens)
        }
    }

    @Test
    fun `placing a queen marks its row column and diagonals as attacked`() = runTest {
        val viewModel = createViewModel()
        viewModel.onAction(QueenGameAction.TileClick(BoardPosition(x = 0, y = 0)))

        viewModel.viewState.test {
            val attackMap = awaitItem().queenAttackMap

            assertTrue(attackMap.isAttacked(x = 3, y = 0))
            assertTrue(attackMap.isAttacked(x = 0, y = 3))
            assertTrue(attackMap.isAttacked(x = 2, y = 2))
            assertFalse(attackMap.isAttacked(x = 1, y = 2))
        }
    }

    @Test
    fun `two queens on the same row are conflicted`() = runTest {
        val viewModel = createViewModel()
        viewModel.onAction(QueenGameAction.TileClick(BoardPosition(x = 0, y = 0)))
        viewModel.onAction(QueenGameAction.TileClick(BoardPosition(x = 2, y = 0)))

        viewModel.viewState.test {
            val attackMap = awaitItem().queenAttackMap

            assertTrue(attackMap.isConflicted(x = 0, y = 0))
            assertTrue(attackMap.isConflicted(x = 2, y = 0))
        }
    }

    @Test
    fun `a full board with conflicts is not solved`() = runTest {
        val viewModel = createViewModel()
        listOf(
            BoardPosition(x = 0, y = 0),
            BoardPosition(x = 1, y = 1),
            BoardPosition(x = 2, y = 2),
            BoardPosition(x = 3, y = 3),
        ).forEach { viewModel.onAction(QueenGameAction.TileClick(it)) }

        viewModel.viewState.test {
            assertFalse(awaitItem().isSolved)
        }
        assertTrue(repository.inserted.isEmpty())
    }

    @Test
    fun `a partially filled board is not solved`() = runTest {
        val viewModel = createViewModel()
        viewModel.onAction(QueenGameAction.TileClick(BoardPosition(x = 1, y = 0)))
        viewModel.onAction(QueenGameAction.TileClick(BoardPosition(x = 3, y = 1)))

        viewModel.viewState.test {
            assertFalse(awaitItem().isSolved)
        }
        assertTrue(repository.inserted.isEmpty())
    }

    @Test
    fun `placing every queen without conflicts solves the board`() = runTest {
        val viewModel = createViewModel()

        solve(viewModel)

        viewModel.viewState.test {
            assertTrue(awaitItem().isSolved)
        }
    }

    @Test
    fun `solving stores a single result with the board size`() = runTest {
        val viewModel = createViewModel()

        solve(viewModel)

        assertEquals(1, repository.inserted.size)
        assertEquals(4, repository.inserted.single().boardSize)
    }

    @Test
    fun `solving stores how long the game took`() = runTest {
        val viewModel = createViewModel()
        timeProvider.advanceBy(12_000)

        solve(viewModel)

        assertEquals(12_000, repository.inserted.single().duration)
    }

    @Test
    fun `removing and replacing the last queen stores a second result`() = runTest {
        val viewModel = createViewModel()
        solve(viewModel)
        val lastQueen = BoardPosition(x = 2, y = 3)

        viewModel.onAction(QueenGameAction.TileClick(lastQueen))
        viewModel.onAction(QueenGameAction.TileClick(lastQueen))

        assertEquals(2, repository.inserted.size)
    }

    @Test
    fun `placing more queens than the board holds is ignored`() = runTest {
        val viewModel = createViewModel()
        solve(viewModel)

        viewModel.onAction(QueenGameAction.TileClick(BoardPosition(x = 0, y = 0)))

        viewModel.viewState.test {
            assertEquals(4, awaitItem().queens.size)
        }
    }

    @Test
    fun `removing a queen frees a slot for another one`() = runTest {
        val viewModel = createViewModel()
        solve(viewModel)
        val placed = BoardPosition(x = 1, y = 0)
        val free = BoardPosition(x = 0, y = 0)

        viewModel.onAction(QueenGameAction.TileClick(placed))
        viewModel.onAction(QueenGameAction.TileClick(free))

        viewModel.viewState.test {
            val queens = awaitItem().queens
            assertEquals(4, queens.size)
            assertTrue(queens.contains(free))
        }
    }

    @Test
    fun `restart clears the board`() = runTest {
        val viewModel = createViewModel()
        solve(viewModel)

        viewModel.viewState.test {
            assertTrue(awaitItem().isSolved)

            viewModel.onAction(QueenGameAction.RestartClick)

            val state = awaitItem()
            assertEquals(emptySet<BoardPosition>(), state.queens)
            assertFalse(state.isSolved)
        }
    }

    @Test
    fun `restart keeps the board size and queen color from the route`() = runTest {
        val viewModel = createViewModel(boardSize = 6, queenColor = QueenColor.Black)
        viewModel.onAction(QueenGameAction.TileClick(BoardPosition(x = 0, y = 0)))

        viewModel.viewState.test {
            assertEquals(1, awaitItem().queens.size)

            viewModel.onAction(QueenGameAction.RestartClick)

            val state = awaitItem()
            assertEquals(6, state.board.size)
            assertEquals(QueenColor.Black, state.queenColor)
        }
    }

    @Test
    fun `restart measures the next game from scratch`() = runTest {
        val viewModel = createViewModel()
        timeProvider.advanceBy(30_000)
        viewModel.onAction(QueenGameAction.RestartClick)

        timeProvider.advanceBy(5_000)
        solve(viewModel)

        assertEquals(5_000, repository.inserted.single().duration)
    }

    @Test
    fun `elapsed time reports the time since the game started`() = runTest {
        val viewModel = createViewModel()

        viewModel.elapsedMilliseconds.test {
            assertEquals(0L, awaitItem())

            timeProvider.advanceBy(1_000)

            assertEquals(1_000L, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun solve(viewModel: QueenGameViewModel) {
        FOUR_QUEENS_SOLUTION.forEach { viewModel.onAction(QueenGameAction.TileClick(it)) }
    }

    private fun createViewModel(
        boardSize: Int = 4,
        queenColor: QueenColor = QueenColor.White,
    ): QueenGameViewModel = QueenGameViewModel(
        route = QueenGameKey(boardSize = boardSize, queenColor = queenColor),
        gameResultRepository = repository,
        timeProvider = timeProvider,
    )
}

private val FOUR_QUEENS_SOLUTION = listOf(
    BoardPosition(x = 1, y = 0),
    BoardPosition(x = 3, y = 1),
    BoardPosition(x = 0, y = 2),
    BoardPosition(x = 2, y = 3),
)
