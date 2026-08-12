package com.jaxjack.queens.features.queengame.game

import androidx.lifecycle.ViewModel
import com.jaxjack.queens.features.queengame.game.navigation.QueenGameKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel(assistedFactory = QueenGameViewModel.Factory::class)
class QueenGameViewModel @AssistedInject constructor(
    @Assisted val route: QueenGameKey
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(key: QueenGameKey): QueenGameViewModel
    }

    private val _viewState = MutableStateFlow(
        QueenGameViewState(
            boardSize = route.boardSize
        )
    )
    val viewState: StateFlow<QueenGameViewState> = _viewState.asStateFlow()

    fun onAction(action: QueenGameAction) = when (action) {
        else -> {}
    }
}

data class QueenGameViewState(
    val boardSize: Int
)

sealed interface QueenGameAction {
}