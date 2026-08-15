package com.jaxjack.queens.features.leaderboard.impl

import app.cash.turbine.test
import com.jaxjack.queens.core.testing.FakeGameResultRepository
import com.jaxjack.queens.core.testing.TestCoroutineExtension
import com.jaxjack.queens.gameresult.api.GameResult
import com.jaxjack.queens.gameresult.api.GameResultDraft
import com.jaxjack.queens.gameresult.api.GameResultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TestCoroutineExtension::class)
class LeaderboardViewModelTest {

    private val repository = FakeGameResultRepository()

    @Test
    fun `starts in a loading state`() = runTest {
        val viewModel = LeaderboardViewModel(repository)

        viewModel.viewState.test {
            val state = awaitItem()

            assertTrue(state.isLoading)
            assertEquals(emptyList<GameResult>(), state.list)
            assertNull(state.error)
        }
    }

    @Test
    fun `stops loading and exposes the results once they arrive`() = runTest {
        val viewModel = LeaderboardViewModel(repository)

        viewModel.viewState.test {
            assertTrue(awaitItem().isLoading)

            repository.emit(listOf(RESULT_FOUR, RESULT_SIX))

            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(listOf(RESULT_FOUR, RESULT_SIX), state.list)
            assertNull(state.error)
        }
    }

    @Test
    fun `an empty result set is not a loading state`() = runTest {
        val viewModel = LeaderboardViewModel(repository)

        viewModel.viewState.test {
            assertTrue(awaitItem().isLoading)

            repository.emit(emptyList())

            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(emptyList<GameResult>(), state.list)
            assertNull(state.error)
        }
    }

    @Test
    fun `keeps the order provided by the repository`() = runTest {
        val viewModel = LeaderboardViewModel(repository)

        viewModel.viewState.test {
            awaitItem()

            repository.emit(listOf(RESULT_SIX, RESULT_FOUR))

            assertEquals(listOf(RESULT_SIX, RESULT_FOUR), awaitItem().list)
        }
    }

    @Test
    fun `reflects results added after the first emission`() = runTest {
        val viewModel = LeaderboardViewModel(repository)

        viewModel.viewState.test {
            awaitItem()

            repository.emit(listOf(RESULT_FOUR))
            assertEquals(listOf(RESULT_FOUR), awaitItem().list)

            repository.emit(listOf(RESULT_FOUR, RESULT_SIX))
            assertEquals(listOf(RESULT_FOUR, RESULT_SIX), awaitItem().list)
        }
    }

    @Test
    fun `exposes the error when observing fails`() = runTest {
        val failure = IllegalStateException("database unavailable")
        val viewModel = LeaderboardViewModel(FailingGameResultRepository(failure))

        viewModel.viewState.test {
            val state = awaitItem()

            assertFalse(state.isLoading)
            assertSame(failure, state.error)
            assertEquals(emptyList<GameResult>(), state.list)
        }
    }
}

private class FailingGameResultRepository(
    private val throwable: Throwable,
) : GameResultRepository {

    override suspend fun insert(gameResultDraft: GameResultDraft) = Unit

    override fun observeBestPerBoardSize(): Flow<List<GameResult>> = flow { throw throwable }
}

private val RESULT_FOUR = GameResult(id = 1, duration = 12_000, boardSize = 4)
private val RESULT_SIX = GameResult(id = 2, duration = 45_000, boardSize = 6)
