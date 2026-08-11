package com.jaxjack.queens.features.queengame.configuration

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GameConfigurationViewModel @Inject constructor() : ViewModel() {

    private val _viewState = MutableStateFlow(GameConfigurationViewState())
    val viewState: StateFlow<GameConfigurationViewState> = _viewState.asStateFlow()

    fun onAction(action: GameConfigurationAction) = when (action) {
        is GameConfigurationAction.ConfigMaximumBoardSizeForScreen -> handleMaximumBoardSize(action.boardSize)
        GameConfigurationAction.DecreaseButtonClick -> changeBoardSize(-1)
        GameConfigurationAction.IncreaseButtonClick -> changeBoardSize(+1)
    }

    private fun changeBoardSize(change: Int) {
        _viewState.update { state ->
            val newBoardSize = (state.boardSize + change)
                .coerceIn(MIN_BOARD_SIZE, state.maximumBoardSize)

            state.copy(boardSize = newBoardSize,)
        }
    }

    private fun handleMaximumBoardSize(maximumBoardSize: Int) {
        _viewState.update { state -> state.copy(maximumBoardSize = maximumBoardSize) }
    }
}


private const val MIN_BOARD_SIZE = 4

data class GameConfigurationViewState(
    val boardSize: Int = 4,
    val maximumBoardSize: Int = 8,
) {
    val decreaseButtonEnabled: Boolean = boardSize > MIN_BOARD_SIZE
    val increaseButtonEnabled: Boolean = boardSize < maximumBoardSize
}

sealed interface GameConfigurationAction {
    data class ConfigMaximumBoardSizeForScreen(val boardSize: Int) : GameConfigurationAction
    data object DecreaseButtonClick : GameConfigurationAction
    data object IncreaseButtonClick : GameConfigurationAction
}