package com.jaxjack.queens.features.queengame.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.jaxjack.queens.board.ui.BoardParams
import com.jaxjack.queens.styleguide.R

@Composable
internal fun QueenGameViewState.toParams(): QueenGameParams {
    return QueenGameParams(
        boardParams = BoardParams(
            board = board
        ),
        tiles = board.positions.associateWith { position ->
            val hasQueen = queens.contains(position)
            val isAttacked = queenAttackMap.isAttacked(position.x, position.y)
            val isConflicted = queenAttackMap.isConflicted(position.x, position.y)
            QueenGameTileParams(
                // TODO move colors to the styleguide
                backgroundColor = when {
                    isConflicted -> Color.Red.copy(alpha = 0.5f)
                    isAttacked -> Color.Gray.copy(alpha = 0.8f)
                    else -> Color.Transparent
                },
                icon = painterResource(R.drawable.queen)
                    .takeIf { hasQueen },
            )
        }
    )
}