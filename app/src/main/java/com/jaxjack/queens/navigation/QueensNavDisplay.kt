package com.jaxjack.queens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jaxjack.queens.features.queengame.configuration.navigation.GameConfigurationKey
import com.jaxjack.queens.features.queengame.configuration.navigation.gameConfigurationEntry
import com.jaxjack.queens.features.queengame.game.navigation.QueenGameKey
import com.jaxjack.queens.features.queengame.game.navigation.queenGameEntry

@Composable
fun QueensNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(GameConfigurationKey)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            gameConfigurationEntry(
                onPlayClick = { backStack.add(QueenGameKey(boardSize = it)) },
            )
            queenGameEntry(
                onBackClick = { backStack.removeLastOrNull() }
            )
        },
    )
}
