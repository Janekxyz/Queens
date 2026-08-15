package com.jaxjack.queens.features.leaderboard.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaxjack.queens.features.gameresult.api.GameResult
import com.jaxjack.queens.features.gameresult.api.GameResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    gameResultRepository: GameResultRepository
) : ViewModel() {

    val viewState = gameResultRepository.observeBestPerBoardSize()
        .map { results -> LeaderboardViewState(isLoading = false, list = results) }
        .catch { throwable -> emit(LeaderboardViewState(isLoading = false, error = throwable)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LeaderboardViewState()
        )
}

data class LeaderboardViewState(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val list: List<GameResult> = emptyList()
)