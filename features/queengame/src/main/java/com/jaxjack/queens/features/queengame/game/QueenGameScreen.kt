package com.jaxjack.queens.features.queengame.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaxjack.queens.board.BoardContent
import com.jaxjack.queens.board.BoardParams
import com.jaxjack.queens.styleguide.R

@Composable
internal fun QueenGameScreen(
    modifier: Modifier,
    viewModel: QueenGameViewModel,
    onBackClick: () -> Unit
) {

    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val params = state.toParams()

    QueenGameContent(
        modifier = modifier,
        params = params,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QueenGameContent(
    modifier: Modifier,
    params: QueenGameParams,
    onBackClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            windowInsets = WindowInsets.navigationBars.union(WindowInsets.statusBars),
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        // TODO implement content description
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryFixed
                    )
                }
            },
            title = {}
        )

        // Board
        BoardContent(params = params.boardParams)

    }
}

@Immutable
data class QueenGameParams(
    val boardParams: BoardParams
)
