package com.jaxjack.queens.features.queengame.configuration

import app.cash.turbine.test
import com.jaxjack.queens.core.testing.TestCoroutineExtension
import com.jaxjack.queens.features.queengame.QueenColor
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TestCoroutineExtension::class)
class GameConfigurationViewModelTest {

    @Test
    fun `starts at the minimum board size with a white queen`() = runTest {
        val viewModel = createViewModel()

        viewModel.viewState.test {
            val item = awaitItem()
            assertEquals(MIN_BOARD_SIZE, item.boardSize)
            assertEquals(DEFAULT_MAX_BOARD_SIZE, item.maximumBoardSize)
            assertEquals(QueenColor.White, item.queenColor)
        }
    }

    @Test
    fun `increase raises the board size by one`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(GameConfigurationAction.IncreaseButtonClick)

        viewModel.viewState.test {
            assertEquals(MIN_BOARD_SIZE + 1, awaitItem().boardSize)
        }
    }

    @Test
    fun `decrease lowers the board size by one`() = runTest {
        val viewModel = createViewModel()
        viewModel.onAction(GameConfigurationAction.IncreaseButtonClick)

        viewModel.onAction(GameConfigurationAction.DecreaseButtonClick)

        viewModel.viewState.test {
            assertEquals(MIN_BOARD_SIZE, awaitItem().boardSize)
        }
    }

    @Test
    fun `decrease at the minimum keeps the board size and emits nothing`() = runTest {
        val viewModel = createViewModel()

        viewModel.viewState.test {
            assertEquals(MIN_BOARD_SIZE, awaitItem().boardSize)

            viewModel.onAction(GameConfigurationAction.DecreaseButtonClick)
            viewModel.onAction(GameConfigurationAction.IncreaseButtonClick)

            assertEquals(MIN_BOARD_SIZE + 1, awaitItem().boardSize)
        }
    }

    @Test
    fun `increase at the maximum keeps the board size and emits nothing`() = runTest {
        val viewModel = createViewModel()
        repeat(DEFAULT_MAX_BOARD_SIZE - MIN_BOARD_SIZE) {
            viewModel.onAction(GameConfigurationAction.IncreaseButtonClick)
        }

        viewModel.viewState.test {
            assertEquals(DEFAULT_MAX_BOARD_SIZE, awaitItem().boardSize)

            viewModel.onAction(GameConfigurationAction.IncreaseButtonClick)
            viewModel.onAction(GameConfigurationAction.DecreaseButtonClick)

            assertEquals(DEFAULT_MAX_BOARD_SIZE - 1, awaitItem().boardSize)
        }
    }

    @Test
    fun `decrease button is disabled only at the minimum`() = runTest {
        val viewModel = createViewModel()

        viewModel.viewState.test {
            assertFalse(awaitItem().decreaseButtonEnabled)

            viewModel.onAction(GameConfigurationAction.IncreaseButtonClick)

            assertTrue(awaitItem().decreaseButtonEnabled)
        }
    }

    @Test
    fun `increase button is disabled only at the maximum`() = runTest {
        val viewModel = createViewModel()

        viewModel.viewState.test {
            assertTrue(awaitItem().increaseButtonEnabled)

            repeat(DEFAULT_MAX_BOARD_SIZE - MIN_BOARD_SIZE) {
                viewModel.onAction(GameConfigurationAction.IncreaseButtonClick)
            }

            assertFalse(expectMostRecentItem().increaseButtonEnabled)
        }
    }

    @Test
    fun `configuring the screen maximum caps how far the board size can grow`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(GameConfigurationAction.ConfigMaximumBoardSizeForScreen(boardSize = 5))
        repeat(times = 10) { viewModel.onAction(GameConfigurationAction.IncreaseButtonClick) }

        viewModel.viewState.test {
            val item = awaitItem()
            assertEquals(5, item.maximumBoardSize)
            assertEquals(5, item.boardSize)
            assertFalse(item.increaseButtonEnabled)
        }
    }

    @Test
    fun `lowering the screen maximum below the current board size leaves the board size untouched`() =
        runTest {
            val viewModel = createViewModel()
            repeat(times = 4) { viewModel.onAction(GameConfigurationAction.IncreaseButtonClick) }

            viewModel.onAction(GameConfigurationAction.ConfigMaximumBoardSizeForScreen(boardSize = 5))

            viewModel.viewState.test {
                val item = awaitItem()
                assertEquals(DEFAULT_MAX_BOARD_SIZE, item.boardSize)
                assertEquals(5, item.maximumBoardSize)
            }
        }

    @Test
    fun `queen color click selects the clicked color`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(GameConfigurationAction.QueenColorClick(QueenColor.Black))
        viewModel.viewState.test {
            assertEquals(QueenColor.Black, awaitItem().queenColor)
        }
    }

    @Test
    fun `queen color click does not change the board size`() = runTest {
        val viewModel = createViewModel()
        viewModel.onAction(GameConfigurationAction.IncreaseButtonClick)

        viewModel.onAction(GameConfigurationAction.QueenColorClick(QueenColor.Black))

        viewModel.viewState.test {
            assertEquals(MIN_BOARD_SIZE + 1, awaitItem().boardSize)
        }
    }

    @Test
    fun `selecting the already selected color emits nothing`() = runTest {
        val viewModel = createViewModel()

        viewModel.viewState.test {
            assertEquals(QueenColor.White, awaitItem().queenColor)

            viewModel.onAction(GameConfigurationAction.QueenColorClick(QueenColor.White))
            viewModel.onAction(GameConfigurationAction.QueenColorClick(QueenColor.Black))

            assertEquals(QueenColor.Black, awaitItem().queenColor)
        }
    }

    private fun createViewModel(): GameConfigurationViewModel {
        return GameConfigurationViewModel()
    }
}

private const val MIN_BOARD_SIZE = 4
private const val DEFAULT_MAX_BOARD_SIZE = 8
