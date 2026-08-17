package com.jaxjack.queens.features.queengame.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.jaxjack.queens.features.queengame.QueenColor
import com.jaxjack.queens.features.queengame.tint
import com.jaxjack.queens.styleguide.R
import com.jaxjack.queens.styleguide.theme.QueensTheme

@Composable
fun GameConfigurationViewState.toParams(): GameConfigurationParams {
    return GameConfigurationParams(
        boardSizeText = boardSize.toString(),
        boardDimensionsText = stringResource(R.string.game_configuration_board_dimensions, boardSize),
        decreaseButtonContainerColor = if (decreaseButtonEnabled) {
            QueensTheme.colors.tileDark
        } else {
            QueensTheme.colors.tileLight
        },
        decreaseButtonEnabled = decreaseButtonEnabled,
        increaseButtonContainerColor = if (increaseButtonEnabled) {
            QueensTheme.colors.tileDark
        } else {
            QueensTheme.colors.tileLight
        },
        increaseButtonEnabled = increaseButtonEnabled,
        queenColorOptions = QueenColorOptionsParams(QueenColor.entries.map { color ->
            QueenColorOptionParams(
                queenColor = color,
                tint = color.tint,
                backgroundColor = when(color) {
                    QueenColor.White -> QueensTheme.colors.tileDark
                    QueenColor.Black -> QueensTheme.colors.tileLight
                },
                borderColor = if (color == queenColor) {
                    QueensTheme.colors.border
                } else {
                    Color.Transparent
                },
                contentDescription = stringResource(color.contentDescription),
            )
        })
    )
}

private val QueenColor.contentDescription: Int
    get() = when (this) {
        QueenColor.White -> R.string.game_configuration_queen_color_white
        QueenColor.Black -> R.string.game_configuration_queen_color_black
    }
