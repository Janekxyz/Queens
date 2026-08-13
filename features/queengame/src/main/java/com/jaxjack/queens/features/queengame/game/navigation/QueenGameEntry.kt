package com.jaxjack.queens.features.queengame.game.navigation

import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jaxjack.queens.features.queengame.game.QueenGameScreen
import com.jaxjack.queens.features.queengame.game.QueenGameViewModel

fun EntryProviderScope<NavKey>.queenGameEntry(
    onBackClick: () -> Unit,
    onNextGameClick: () -> Unit
) {
    entry<QueenGameKey> { key ->
        val viewModel = hiltViewModel<QueenGameViewModel, QueenGameViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(key)
            }
        )
        QueenGameScreen(
            modifier = Modifier,
            viewModel = viewModel,
            onBackClick = onBackClick,
            onNextGameClick = onNextGameClick
        )
    }
}
