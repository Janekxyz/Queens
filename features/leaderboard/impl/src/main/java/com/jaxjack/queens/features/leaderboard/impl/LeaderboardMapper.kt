package com.jaxjack.queens.features.leaderboard.impl

import com.jaxjack.queens.features.gameresult.api.GameResult

internal fun LeaderboardViewState.toParams(): LeaderboardParams {
    return LeaderboardParams(
        results = list.toParams(),
        isEmpty = list.isEmpty(),
        isError = error != null,
        isLoading = isLoading
    )
}

private fun List<GameResult>.toParams(): List<LeaderboardItemParams> {
    return map {
        LeaderboardItemParams(
            id = it.id,
            boardSize = "${it.boardSize} x ${it.boardSize}",
            duration = it.duration.toDurationString()
        )
    }
}

private fun Long.toDurationString(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        hours > 0 -> "$hours h $minutes min $seconds sec"
        minutes > 0 -> "$minutes min $seconds sec"
        else -> "$seconds sec"
    }
}