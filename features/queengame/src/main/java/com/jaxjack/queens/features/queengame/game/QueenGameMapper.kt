package com.jaxjack.queens.features.queengame.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jaxjack.queens.board.ui.BoardParams
import com.jaxjack.queens.features.queengame.tint
import com.jaxjack.queens.styleguide.R
import com.jaxjack.queens.styleguide.theme.base.boardAttackedDot
import com.jaxjack.queens.styleguide.theme.base.boardAttackedOverlay
import com.jaxjack.queens.styleguide.theme.base.boardQueenConflict

@Composable
internal fun QueenGameViewState.toParams(): QueenGameParams {
    return QueenGameParams(
        boardParams = BoardParams(
            board = board
        ),
        headerParams = QueenGameHeaderParams(
            queensUsed = "${queens.size}/${board.size}"
        ),
        successParams = if (isSolved) {
            QueenGameSuccessParams(
                title = stringResource(R.string.queen_game_success_title),
                message = stringResource(R.string.queen_game_success_message, board.size),
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
                backgroundColor = when {
                    hasQueen && isConflicted -> boardQueenConflict
                    isAttacked && !hasQueen -> boardAttackedOverlay
                    else -> Color.Transparent
                },
                attackedDotColor = boardAttackedDot.takeIf { isAttacked && !hasQueen },
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