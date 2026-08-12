package com.jaxjack.queens.features.queengame.game

import androidx.lifecycle.ViewModel
import com.jaxjack.queens.board.Board
import com.jaxjack.queens.board.BoardPosition
import com.jaxjack.queens.features.queengame.game.data.QueenAttackMap
import com.jaxjack.queens.features.queengame.game.navigation.QueenGameKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = QueenGameViewModel.Factory::class)
class QueenGameViewModel @AssistedInject constructor(
    @Assisted private val route: QueenGameKey,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(key: QueenGameKey): QueenGameViewModel
    }

    private val _viewState = MutableStateFlow(initialState())
    val viewState: StateFlow<QueenGameViewState> = _viewState.asStateFlow()

    fun onAction(action: QueenGameAction) = when (action) {
        QueenGameAction.RestartClick -> restart()
        is QueenGameAction.TileClick -> handleTileClick(action.position)
    }

    private fun restart() {
        _viewState.update { initialState() }
    }

    private fun handleTileClick(position: BoardPosition) {
        val currentState = _viewState.value

        if (currentState.queens.contains(position)) {
            updatePositions { remove(position) }
            return
        }

        updatePositions { add(position) }
    }

    private fun updatePositions(
        action: MutableSet<BoardPosition>.() -> Unit
    ) {
        _viewState.update { state ->
            val queens = state.queens.toMutableSet().apply { action() }
            val attackMap = QueenAttackMap.of(state.board.size, queens)
            state.copy(
                queens = queens,
                queenAttackMap = attackMap
            )
        }
    }

    private fun initialState(): QueenGameViewState = QueenGameViewState(
        board = Board.create(route.boardSize),
        queens = emptySet(),
        queenAttackMap = QueenAttackMap.of(size = route.boardSize, queens = emptySet())
    )
}

data class QueenGameViewState(
    val board: Board,
    val queens: Set<BoardPosition>,
    val queenAttackMap: QueenAttackMap
)

sealed interface QueenGameAction {
    data object RestartClick : QueenGameAction
    data class TileClick(val position: BoardPosition) : QueenGameAction
}
