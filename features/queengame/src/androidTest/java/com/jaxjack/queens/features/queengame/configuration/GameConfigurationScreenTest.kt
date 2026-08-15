package com.jaxjack.queens.features.queengame.configuration

import androidx.annotation.StringRes
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jaxjack.queens.features.queengame.QueenColor
import com.jaxjack.queens.styleguide.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameConfigurationScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private var playedBoardSize: Int? = null
    private var playedQueenColor: QueenColor? = null
    private var leaderboardClicks = 0

    @Test
    fun `starts at the minimum board size`() {
        setConfigurationScreen()

        composeRule.onNodeWithText(boardDimensions(4)).assertIsDisplayed()
    }

    @Test
    fun `increasing the board size updates the dimensions`() {
        setConfigurationScreen()

        clickIncrease()

        composeRule.onNodeWithText(boardDimensions(5)).assertIsDisplayed()
    }

    @Test
    fun `decreasing the board size updates the dimensions`() {
        setConfigurationScreen()
        clickIncrease()

        clickDecrease()

        composeRule.onNodeWithText(boardDimensions(4)).assertIsDisplayed()
    }

    @Test
    fun `decrease is disabled at the minimum board size`() {
        setConfigurationScreen()

        composeRule.onNodeWithContentDescription(string(R.string.content_description_decrease_board_size))
            .assertIsNotEnabled()
    }

    @Test
    fun `decrease becomes enabled above the minimum board size`() {
        setConfigurationScreen()

        clickIncrease()

        composeRule.onNodeWithContentDescription(string(R.string.content_description_decrease_board_size))
            .assertIsEnabled()
    }

    @Test
    fun `play reports the selected board size`() {
        setConfigurationScreen()
        clickIncrease()
        clickIncrease()

        clickPlay()

        assertEquals(6, playedBoardSize)
    }

    @Test
    fun `play reports the white queen by default`() {
        setConfigurationScreen()

        clickPlay()

        assertEquals(QueenColor.White, playedQueenColor)
    }

    @Test
    fun `play reports the selected queen color`() {
        setConfigurationScreen()

        composeRule.onNodeWithContentDescription(string(R.string.game_configuration_queen_color_black))
            .performTouchInput { click() }
        clickPlay()

        assertEquals(QueenColor.Black, playedQueenColor)
    }

    @Test
    fun `the leaderboard button reports a click`() {
        setConfigurationScreen()

        composeRule.onNodeWithContentDescription(string(R.string.content_description_leaderboard))
            .performTouchInput { click() }

        assertEquals(1, leaderboardClicks)
    }

    private fun clickIncrease() {
        composeRule.onNodeWithContentDescription(string(R.string.content_description_increase_board_size))
            .performTouchInput { click() }
    }

    private fun clickDecrease() {
        composeRule.onNodeWithContentDescription(string(R.string.content_description_decrease_board_size))
            .performTouchInput { click() }
    }

    private fun clickPlay() {
        composeRule.onNodeWithText(string(R.string.game_configuration_play_button))
            .performTouchInput { click() }
    }

    private fun boardDimensions(size: Int): String =
        string(R.string.game_configuration_board_dimensions, size)

    private fun string(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)

    private fun setConfigurationScreen() {
        val viewModel = GameConfigurationViewModel()
        composeRule.setContent {
            GameConfigurationScreen(
                modifier = Modifier,
                onPlayClick = { boardSize, queenColor ->
                    playedBoardSize = boardSize
                    playedQueenColor = queenColor
                },
                onLeaderboardClick = { leaderboardClicks++ },
                viewModel = viewModel,
            )
        }
    }
}
