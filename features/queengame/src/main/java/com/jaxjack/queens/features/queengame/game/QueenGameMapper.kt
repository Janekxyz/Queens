package com.jaxjack.queens.features.queengame.game

import com.jaxjack.queens.board.BoardParams

internal fun QueenGameViewState.toParams(): QueenGameParams {
    return QueenGameParams(
        boardParams = BoardParams(
            size = boardSize
        )
    )
}