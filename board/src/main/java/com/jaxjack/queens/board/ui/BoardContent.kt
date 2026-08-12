package com.jaxjack.queens.board.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import com.jaxjack.queens.board.Board
import com.jaxjack.queens.board.BoardPosition
import com.jaxjack.queens.styleguide.theme.base.boardTileGreen
import com.jaxjack.queens.styleguide.theme.base.boardTileWhite
import kotlin.math.min

@Composable
fun BoardContent(
    params: BoardParams,
    cell: @Composable BoxScope.(BoardPosition) -> Unit = {}
) {
    Layout(
        content = {
            params.board.positions.forEach { position ->
                BoardCell(position = position) {
                    cell(position)
                }
            }
        }
    ) { measurables, constraints ->
        val screenSide = min(constraints.maxWidth, constraints.maxHeight)
        val cellSize = screenSide / params.board.size
        val boardSize = cellSize * params.board.size

        val cellConstraints = Constraints.fixed(cellSize, cellSize)
        val placeables = measurables.map { it.measure(cellConstraints) }

        layout(boardSize, boardSize) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(
                    x = (index % params.board.size) * cellSize,
                    y = (index / params.board.size) * cellSize,
                )
            }
        }
    }
}

@Composable
private fun BoardCell(
    position: BoardPosition,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier.drawBehind {
            // TODO color should be moved to the proper class
            drawRect(if ((position.x + position.y) % 2 == 0) boardTileWhite else boardTileGreen)
        },
        contentAlignment = Alignment.Center,
        content = content
    )
}

data class BoardParams(
    val board: Board
)