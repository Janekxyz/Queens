package com.jaxjack.queens.features.queengame.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaxjack.queens.board.Board
import com.jaxjack.queens.board.BoardPosition
import com.jaxjack.queens.core.time.TimeProvider
import com.jaxjack.queens.features.queengame.QueenColor
import com.jaxjack.queens.features.queengame.game.data.QueenAttackMap
import com.jaxjack.queens.features.queengame.game.navigation.QueenGameKey
import com.jaxjack.queens.gameresult.api.GameResultDraft
import com.jaxjack.queens.gameresult.api.GameResultRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
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

    private val startedAt = MutableStateFlow(timeProvider.elapsedRealtimeMillis())

    private val finalDuration: Flow<Long?> = _viewState
        .map { state -> state.solvedDuration.takeIf { state.isSolved } }
        .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val elapsedMilliseconds: StateFlow<Long> = combine(startedAt, finalDuration, ::Pair)
        .flatMapLatest { (start, finalDuration) ->
            finalDuration?.let { flowOf(it) } ?: ticker(start)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun ticker(startedAt: Long): Flow<Long> = flow {
        while (true) {
            emit(timeProvider.elapsedRealtimeMillis() - startedAt)
            delay(1.seconds)
        }
    }

    fun onAction(action: QueenGameAction) = when (action) {
        QueenGameAction.RestartClick -> restart()
        is QueenGameAction.TileClick -> handleTileClick(action.position)
    }

    private fun restart() {
        startedAt.value = timeProvider.elapsedRealtimeMillis()
        _viewState.update { initialState() }
    }

    private fun handleTileClick(position: BoardPosition) {
        val currentState = _viewState.value

        if (currentState.queens.contains(position)) {
            updatePositions { remove(position) }
            return
        }

        if (currentState.queens.size >= currentState.board.size) return

        updatePositions { add(position) }
    }

    private fun updatePositions(
        action: MutableSet<BoardPosition>.() -> Unit
    ) {
        val wasSolved = _viewState.value.isSolved
        val state = _viewState.updateAndGet { state ->
            val queens = state.queens.toMutableSet().apply { action() }
            val updated = state.copy(
                queens = queens,
                queenAttackMap = QueenAttackMap.of(state.board.size, queens)
            )

            if (!wasSolved && updated.isSolved) {
                updated.copy(solvedDuration = timeProvider.elapsedRealtimeMillis() - startedAt.value)
            } else {
                updated
            }
        }

        if (!wasSolved && state.isSolved) {
            saveResult(duration = state.solvedDuration, boardSize = state.board.size)
        }
    }

    private fun saveResult(duration: Long, boardSize: Int) {
        viewModelScope.launch {
            val gameResult = GameResultDraft(
                duration = duration,
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
    val queenColor: QueenColor,
    val solvedDuration: Long = 0,
) {
    val isSolved: Boolean = queens.size == board.size &&
            queens.none { queenAttackMap.isConflicted(it.x, it.y) }
}

sealed interface QueenGameAction {
    data object RestartClick : QueenGameAction
    data class TileClick(val position: BoardPosition) : QueenGameAction
}
