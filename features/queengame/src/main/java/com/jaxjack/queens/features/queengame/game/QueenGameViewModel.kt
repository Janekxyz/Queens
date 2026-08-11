package com.jaxjack.queens.features.queengame.game

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class QueenGameViewModel @Inject constructor() : ViewModel() {

    private val _viewState = MutableStateFlow(QueenGameViewState())
    val viewState: StateFlow<QueenGameViewState> = _viewState.asStateFlow()

    fun onAction(action: QueenGameAction) = when (action) {
        else -> {}
    }
}

data class QueenGameViewState(
    val tmp: Int = 0
)

sealed interface QueenGameAction {
}