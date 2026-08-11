package com.jaxjack.queens.features.queengame.configuration.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jaxjack.queens.features.queengame.configuration.GameConfigurationScreen

/**
 * Registers the board setup destination.
 *
 * The caller decides what navigating away means; the screen itself stays
 * internal to this module.
 */
fun EntryProviderScope<NavKey>.gameConfigurationEntry(
    onPlayClick: () -> Unit,
) {
    entry<GameConfigurationKey> {
        GameConfigurationScreen(
            modifier = Modifier,
            onPlayClick = onPlayClick,
        )
    }
}
