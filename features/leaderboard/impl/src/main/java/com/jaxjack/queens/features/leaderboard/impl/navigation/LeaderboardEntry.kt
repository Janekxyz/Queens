package com.jaxjack.queens.features.leaderboard.impl.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jaxjack.queens.features.leaderboard.api.navigation.LeaderboardKey
import com.jaxjack.queens.features.leaderboard.impl.LeaderboardScreen

fun EntryProviderScope<NavKey>.leaderboardEntry(
    onBackClick: () -> Unit
) {
    entry<LeaderboardKey> {
        LeaderboardScreen(
            modifier = Modifier,
            onBackClick = onBackClick
        )
    }
}
