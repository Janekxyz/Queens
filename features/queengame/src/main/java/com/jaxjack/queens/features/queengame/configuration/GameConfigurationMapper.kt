package com.jaxjack.queens.features.queengame.configuration

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.jaxjack.queens.features.queengame.QueenColor
import com.jaxjack.queens.features.queengame.tint
import com.jaxjack.queens.styleguide.R
import com.jaxjack.queens.styleguide.theme.base.boardAttackedDot
import com.jaxjack.queens.styleguide.theme.base.boardTileGreen
import com.jaxjack.queens.styleguide.theme.base.boardTileWhite

@Composable
fun GameConfigurationViewState.toParams(): GameConfigurationParams {
    return GameConfigurationParams(
        boardSizeText = boardSize.toString(),
        boardDimensionsText = stringResource(R.string.game_configuration_board_dimensions, boardSize),
        decreaseButtonContainerColor = if (decreaseButtonEnabled) {
            boardTileGreen
        } else {
            boardTileWhite
        },
        decreaseButtonEnabled = decreaseButtonEnabled,
        increaseButtonContainerColor = if (increaseButtonEnabled) {
            boardTileGreen
        } else {
            boardTileWhite
        },
        increaseButtonEnabled = increaseButtonEnabled,
        queenColorOptions = QueenColor.entries.map { color ->
            QueenColorOptionParams(
                queenColor = color,
                tint = color.tint,
                backgroundColor = when(color) {
                    QueenColor.White -> boardTileGreen
                    QueenColor.Black -> boardTileWhite
                },
                borderColor = if (color == queenColor) {
                    boardAttackedDot
                } else {
                    Color.Transparent
                },
                contentDescription = stringResource(color.contentDescription),
            )
        }
    )
}

private val QueenColor.contentDescription: Int
    get() = when (this) {
        QueenColor.White -> R.string.game_configuration_queen_color_white
        QueenColor.Black -> R.string.game_configuration_queen_color_black
    }
