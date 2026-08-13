package com.jaxjack.queens.features.queengame.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.jaxjack.queens.board.ui.BoardParams
import com.jaxjack.queens.features.queengame.tint
import com.jaxjack.queens.styleguide.R
import com.jaxjack.queens.styleguide.theme.base.boardAttackedDot
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
        tiles = board.positions.associateWith { position ->
            val hasQueen = queens.contains(position)
            val isAttacked = queenAttackMap.isAttacked(position.x, position.y)
            val isConflicted = queenAttackMap.isConflicted(position.x, position.y)
            QueenGameTileParams(
                backgroundColor = if (hasQueen && isConflicted) {
                    boardQueenConflict
                } else {
                    Color.Transparent
                },
                attackedDotColor = boardAttackedDot.takeIf { isAttacked && !hasQueen },
                icon = painterResource(R.drawable.ic_queen)
                    .takeIf { hasQueen },
                iconTint = queenColor.tint,
            )
        }
    )
}