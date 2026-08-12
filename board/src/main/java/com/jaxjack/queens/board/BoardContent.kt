package com.jaxjack.queens.board

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import com.jaxjack.queens.styleguide.theme.base.boardTileGreen
import com.jaxjack.queens.styleguide.theme.base.boardTileWhite
import kotlin.math.min

@Composable
fun BoardContent(params: BoardParams) {
    Layout(
        content = { repeat(params.size * params.size) { BoardCell(it, params.size) } }
    ) { measurables, constraints ->
        val screenSide = min(constraints.maxWidth, constraints.maxHeight)
        val cellSize = screenSide / params.size
        val boardSize = cellSize * params.size

        val cellConstraints = Constraints.fixed(cellSize, cellSize)
        val placeables = measurables.map { it.measure(cellConstraints) }

        layout(boardSize, boardSize) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(
                    x = (index % params.size) * cellSize,
                    y = (index / params.size) * cellSize,
                )
            }
        }
    }
}

@Composable
private fun BoardCell(index: Int, size: Int) {
    val x = index % size
    val y = index / size
    Box(
        modifier = Modifier.drawBehind {
            // TODO color should be moved to the proper class
            drawRect(if ((x + y) % 2 == 0) boardTileWhite else boardTileGreen)
        }
    )
}

data class BoardParams(
    val size: Int
)