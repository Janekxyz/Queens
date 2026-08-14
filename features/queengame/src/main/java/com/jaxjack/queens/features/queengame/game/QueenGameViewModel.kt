package com.jaxjack.queens.features.queengame.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaxjack.queens.board.Board
import com.jaxjack.queens.board.BoardPosition
import com.jaxjack.queens.core.time.TimeProvider
import com.jaxjack.queens.features.gameresult.api.GameResultDraft
import com.jaxjack.queens.features.gameresult.api.GameResultRepository
import com.jaxjack.queens.features.queengame.QueenColor
import com.jaxjack.queens.features.queengame.game.data.QueenAttackMap
import com.jaxjack.queens.features.queengame.game.navigation.QueenGameKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = QueenGameViewModel.Factory::class)
class QueenGameViewModel @AssistedInject constructor(
    @Assisted private val route: QueenGameKey,
    private val gameResultRepository: GameResultRepository,
    private val timeProvider: TimeProvider
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(key: QueenGameKey): QueenGameViewModel
    }

    private val _viewState = MutableStateFlow(initialState())
    val viewState: StateFlow<QueenGameViewState> = _viewState.asStateFlow()

    private var startedAt = timeProvider.elapsedRealtimeMillis()

    val elapsedMilliseconds: StateFlow<Long> = flow {
        while (true) {
            emit(timeProvider.elapsedRealtimeMillis() - startedAt)
            delay(1.seconds)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

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
        val wasSolved = _viewState.value.isSolved
        val state = _viewState.updateAndGet { state ->
            val queens = state.queens.toMutableSet().apply { action() }
            val attackMap = QueenAttackMap.of(state.board.size, queens)
            state.copy(
                queens = queens,
                queenAttackMap = attackMap
            )
        }

        if (!wasSolved && state.isSolved) {
            saveResult(boardSize = state.board.size)
        }
    }

    private fun saveResult(boardSize: Int) {
        viewModelScope.launch {
            val gameResult = GameResultDraft(
                duration = elapsedMilliseconds.value,
                boardSize = boardSize
            )
            gameResultRepository.insert(gameResult)
        }
    }

    private fun initialState(): QueenGameViewState = QueenGameViewState(
        board = Board.create(route.boardSize),
        queens = emptySet(),
        queenAttackMap = QueenAttackMap.of(size = route.boardSize, queens = emptySet()),
        queenColor = route.queenColor
    )
}

data class QueenGameViewState(
    val board: Board,
    val queens: Set<BoardPosition>,
    val queenAttackMap: QueenAttackMap,
    val queenColor: QueenColor
) {
    val isSolved: Boolean = queens.size == board.size &&
            queens.none { queenAttackMap.isConflicted(it.x, it.y) }
}

sealed interface QueenGameAction {
    data object RestartClick : QueenGameAction
    data class TileClick(val position: BoardPosition) : QueenGameAction
}
