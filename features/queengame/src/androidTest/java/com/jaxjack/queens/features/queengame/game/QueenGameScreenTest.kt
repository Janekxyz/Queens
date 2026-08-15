package com.jaxjack.queens.features.queengame.game

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.annotation.StringRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jaxjack.queens.board.BoardPosition
import com.jaxjack.queens.board.ui.boardCellTestTag
import com.jaxjack.queens.core.time.TimeProvider
import com.jaxjack.queens.features.gameresult.api.GameResult
import com.jaxjack.queens.features.gameresult.api.GameResultDraft
import com.jaxjack.queens.features.gameresult.api.GameResultRepository
import com.jaxjack.queens.features.queengame.QueenColor
import com.jaxjack.queens.features.queengame.game.navigation.QueenGameKey
import com.jaxjack.queens.styleguide.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QueenGameScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val repository = RecordingGameResultRepository()
    private val timeProvider = MutableTimeProvider()

    @Test
    fun `shows how many queens are still to place`() {
        setGameScreen()

        composeRule.onNodeWithText(string(R.string.queen_game_queens_left, 4)).assertIsDisplayed()
    }

    @Test
    fun `placing a queen lowers the counter`() {
        setGameScreen()

        tapCell(x = 0, y = 0)

        composeRule.onNodeWithText(string(R.string.queen_game_queens_left, 3)).assertIsDisplayed()
    }

    @Test
    fun `tapping a queen again removes it`() {
        setGameScreen()

        tapCell(x = 0, y = 0)
        tapCell(x = 0, y = 0)

        composeRule.onNodeWithText(string(R.string.queen_game_queens_left, 4)).assertIsDisplayed()
    }

    @Test
    fun `filling the board with conflicts does not show the success overlay`() {
        setGameScreen()

        tapCell(x = 0, y = 0)
        tapCell(x = 1, y = 1)
        tapCell(x = 2, y = 2)
        tapCell(x = 3, y = 3)

        composeRule.onNodeWithText(string(R.string.queen_game_success_title)).assertDoesNotExist()
    }

    @Test
    fun `solving the board shows the success overlay`() {
        setGameScreen()

        solve()

        composeRule.onNodeWithText(string(R.string.queen_game_success_title)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.queen_game_success_restart)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.queen_game_success_next_game)).assertIsDisplayed()
    }

    @Test
    fun `the success overlay reports the achieved time`() {
        setGameScreen()
        timeProvider.elapsedRealtimeMillis = 65_000

        solve()

        composeRule.onNodeWithText("1:05").assertIsDisplayed()
    }

    @Test
    fun `restarting from the overlay clears the board`() {
        setGameScreen()
        solve()

        composeRule.onNodeWithText(string(R.string.queen_game_success_restart)).performTouchInput { click() }

        composeRule.onNodeWithText(string(R.string.queen_game_success_title)).assertDoesNotExist()
        composeRule.onNodeWithText(string(R.string.queen_game_queens_left, 4)).assertIsDisplayed()
    }

    @Test
    fun `next game leaves the screen`() {
        var nextGameClicks = 0
        setGameScreen(onNextGameClick = { nextGameClicks++ })
        solve()

        composeRule.onNodeWithText(string(R.string.queen_game_success_next_game)).performTouchInput { click() }

        assert(nextGameClicks == 1) { "expected one next game click but was $nextGameClicks" }
    }

    private fun string(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)

    private fun solve() {
        FOUR_QUEENS_SOLUTION.forEach { tapCell(x = it.x, y = it.y) }
    }

    private fun tapCell(x: Int, y: Int) {
        composeRule.onNodeWithTag(boardCellTestTag(BoardPosition(x = x, y = y)))
            .performTouchInput { click() }
    }

    private fun setGameScreen(
        onNextGameClick: () -> Unit = {},
    ) {
        val viewModel = QueenGameViewModel(
            route = QueenGameKey(boardSize = 4, queenColor = QueenColor.White),
            gameResultRepository = repository,
            timeProvider = timeProvider,
        )
        composeRule.setContent {
            QueenGameScreen(
                modifier = Modifier,
                viewModel = viewModel,
                onBackClick = {},
                onNextGameClick = onNextGameClick,
            )
        }
    }
}

private class MutableTimeProvider(
    var currentTimeMillis: Long = 0,
    var elapsedRealtimeMillis: Long = 0,
) : TimeProvider {
    override fun currentTimeMillis(): Long = currentTimeMillis
    override fun elapsedRealtimeMillis(): Long = elapsedRealtimeMillis
}

private class RecordingGameResultRepository : GameResultRepository {
    val inserted = mutableListOf<GameResultDraft>()

    override suspend fun insert(gameResultDraft: GameResultDraft) {
        inserted += gameResultDraft
    }

    override fun observeBestPerBoardSize(): Flow<List<GameResult>> = flowOf(emptyList())
}

private val FOUR_QUEENS_SOLUTION = listOf(
    BoardPosition(x = 1, y = 0),
    BoardPosition(x = 3, y = 1),
    BoardPosition(x = 0, y = 2),
    BoardPosition(x = 2, y = 3),
)
