package com.jaxjack.queens.features.queengame.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jaxjack.queens.board.ui.BoardParams
import com.jaxjack.queens.features.queengame.tint
import com.jaxjack.queens.styleguide.R
import com.jaxjack.queens.styleguide.theme.QueensTheme

@Composable
internal fun QueenGameViewState.toParams(): QueenGameParams {
    return QueenGameParams(
        boardParams = BoardParams(
            board = board,
            lightTileColor = QueensTheme.colors.tileLight,
            darkTileColor = QueensTheme.colors.tileDark
        ),
        headerParams = QueenGameHeaderParams(
            queensLeft = stringResource(R.string.queen_game_queens_left, board.size - queens.size)
        ),
        successParams = if (isSolved) {
            QueenGameSuccessParams(
                title = stringResource(R.string.queen_game_success_title),
                message = stringResource(R.string.queen_game_success_message, board.size),
                time = solvedDuration.toTimerParams().time,
                queenTint = queenColor.tint,
                restartButtonText = stringResource(R.string.queen_game_success_restart),
                nextGameButtonText = stringResource(R.string.queen_game_success_next_game)
            )
        } else {
            null
        },
        tiles = board.positions.associateWith { position ->
            val hasQueen = queens.contains(position)
            val isAttacked = queenAttackMap.isAttacked(position.x, position.y)
            val isConflicted = queenAttackMap.isConflicted(position.x, position.y)
            QueenGameTileParams(
                contentDescription = stringResource(
                    when {
                        hasQueen && isConflicted -> R.string.content_description_tile_conflicted
                        hasQueen -> R.string.content_description_tile_queen
                        isAttacked -> R.string.content_description_tile_attacked
                        else -> R.string.content_description_tile_free
                    },
                    position.y + 1,
                    position.x + 1
                ),
                backgroundColor = when {
                    hasQueen && isConflicted -> QueensTheme.colors.conflict
                    isAttacked -> QueensTheme.colors.overlay
                    else -> Color.Transparent
                },
                attackedDotColor = QueensTheme.colors.border.takeIf { isAttacked && !hasQueen },
                icon = painterResource(R.drawable.ic_queen)
                    .takeIf { hasQueen },
                iconTint = queenColor.tint,
            )
        }
    )
}

internal fun Long.toTimerParams() = QueenGameTimerParams(
    time = "%d:%02d".format(this / 60_000, (this / 1_000) % 60)
)