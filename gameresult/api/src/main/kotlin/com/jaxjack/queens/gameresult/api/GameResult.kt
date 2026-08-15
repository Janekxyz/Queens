package com.jaxjack.queens.gameresult.api


/**
 * A completed game that has been stored.
 */
data class GameResult(
    val id: Long,
    val duration: Long,
    val boardSize: Int,
)

/**
 * A completed game that has not been stored yet.
 */
data class GameResultDraft(
    val duration: Long,
    val boardSize: Int,
)