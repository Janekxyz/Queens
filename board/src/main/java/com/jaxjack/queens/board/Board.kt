package com.jaxjack.queens.board

data class Board(
    val size: Int = 0,
    val positions: List<BoardPosition> = emptyList()
) {
    companion object {
        fun create(size: Int) = Board(
            size = size,
            positions = List(size * size) { index ->
                BoardPosition(
                    x = index % size,
                    y = index / size,
                )
            }
        )
    }
}

data class BoardPosition(
    val x: Int,
    val y: Int,
)