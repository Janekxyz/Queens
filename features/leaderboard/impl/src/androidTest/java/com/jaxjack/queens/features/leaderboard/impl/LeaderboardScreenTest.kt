package com.jaxjack.queens.features.leaderboard.impl

import androidx.annotation.StringRes
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jaxjack.queens.gameresult.api.GameResult
import com.jaxjack.queens.gameresult.api.GameResultDraft
import com.jaxjack.queens.gameresult.api.GameResultRepository
import com.jaxjack.queens.styleguide.R
import com.jaxjack.queens.styleguide.theme.QueensTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeaderboardScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val repository = FakeGameResultRepository()
    private var backClicks = 0

    @Test
    fun `shows the empty state when there are no results`() {
        setLeaderboardScreen()

        repository.emit(emptyList())

        composeRule.onNodeWithText(string(R.string.leaderboard_empty)).assertIsDisplayed()
    }

    @Test
    fun `shows a row for every result`() {
        setLeaderboardScreen()

        repository.emit(listOf(RESULT_FOUR, RESULT_SIX))

        composeRule.onNodeWithText("4 x 4").assertIsDisplayed()
        composeRule.onNodeWithText("6 x 6").assertIsDisplayed()
    }

    @Test
    fun `shows the achieved time for every result`() {
        setLeaderboardScreen()

        repository.emit(listOf(RESULT_FOUR, RESULT_SIX))

        composeRule.onNodeWithText("12 sec").assertIsDisplayed()
        composeRule.onNodeWithText("1 min 5 sec").assertIsDisplayed()
    }

    @Test
    fun `does not show the empty state when there are results`() {
        setLeaderboardScreen()

        repository.emit(listOf(RESULT_FOUR))

        composeRule.onNodeWithText(string(R.string.leaderboard_empty)).assertDoesNotExist()
    }

    @Test
    fun `shows the error state when results cannot be read`() {
        setLeaderboardScreen(repository = FailingGameResultRepository())

        val error = string(R.string.leaderboard_error)
        composeRule.waitUntil {
            composeRule.onAllNodesWithText(error).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(error).assertIsDisplayed()
    }

    @Test
    fun `the back button reports a click`() {
        setLeaderboardScreen()

        composeRule.onNodeWithContentDescription(string(R.string.content_description_back))
            .performTouchInput { click() }

        assertEquals(1, backClicks)
    }

    private fun string(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)

    private fun setLeaderboardScreen(repository: GameResultRepository = this.repository) {
        val viewModel = LeaderboardViewModel(repository)
        composeRule.setContent {
            QueensTheme {
                LeaderboardScreen(
                    modifier = Modifier,
                    onBackClick = { backClicks++ },
                    viewModel = viewModel,
                )
            }
        }
    }
}

private class FakeGameResultRepository : GameResultRepository {

    private val results = MutableSharedFlow<List<GameResult>>(replay = 1)

    fun emit(value: List<GameResult>) {
        results.tryEmit(value)
    }

    override suspend fun insert(gameResultDraft: GameResultDraft) = Unit

    override fun observeBestPerBoardSize(): Flow<List<GameResult>> = results
}

private class FailingGameResultRepository : GameResultRepository {

    override suspend fun insert(gameResultDraft: GameResultDraft) = Unit

    override fun observeBestPerBoardSize(): Flow<List<GameResult>> =
        flow { throw IllegalStateException("boom") }
}

private val RESULT_FOUR = GameResult(id = 1, duration = 12_000, boardSize = 4)
private val RESULT_SIX = GameResult(id = 2, duration = 65_000, boardSize = 6)
