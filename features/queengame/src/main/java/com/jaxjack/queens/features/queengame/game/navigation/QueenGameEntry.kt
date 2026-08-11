package com.jaxjack.queens.features.queengame.game.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jaxjack.queens.features.queengame.game.QueenGameScreen

fun EntryProviderScope<NavKey>.queenGameEntry() {
    entry<QueenGameKey> {
        QueenGameScreen(modifier = Modifier)
    }
}
