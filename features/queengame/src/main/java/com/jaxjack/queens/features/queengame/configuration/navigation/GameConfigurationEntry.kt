package com.jaxjack.queens.features.queengame.configuration.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jaxjack.queens.features.queengame.QueenColor
import com.jaxjack.queens.features.queengame.configuration.GameConfigurationScreen

fun EntryProviderScope<NavKey>.gameConfigurationEntry(
    onPlayClick: (Int, QueenColor) -> Unit,
) {
    entry<GameConfigurationKey> {
        GameConfigurationScreen(
            modifier = Modifier,
            onPlayClick = onPlayClick,
        )
    }
}
