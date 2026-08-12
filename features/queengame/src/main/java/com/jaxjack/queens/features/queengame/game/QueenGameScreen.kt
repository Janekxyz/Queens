package com.jaxjack.queens.features.queengame.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaxjack.queens.board.BoardPosition
import com.jaxjack.queens.board.ui.BoardContent
import com.jaxjack.queens.board.ui.BoardParams
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
        onBackClick = onBackClick,
        onTileClick = { viewModel.onAction(QueenGameAction.TileClick(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QueenGameContent(
    modifier: Modifier,
    params: QueenGameParams,
    onBackClick: () -> Unit,
    onTileClick: (BoardPosition) -> Unit
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
        BoardContent(
            params = params.boardParams,
            cell = { position ->
                params.tiles[position]?.let { params ->
                    QueenGameTileContent(
                        params = params,
                        onTileClick = { onTileClick(position) }
                    )
                }
            }
        )
    }
}

@Composable
private fun BoxScope.QueenGameTileContent(
    params: QueenGameTileParams,
    onTileClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(params.backgroundColor)
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(color = backgroundColor)
            .clickable(onClick = onTileClick),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = params.icon != null,
            enter = fadeIn(tween(100)),
            exit = fadeOut(tween(100))
        ) {
            params.icon?.let {
                Icon(
                    modifier = Modifier.fillMaxSize(0.8f),
                    painter = params.icon,
                    contentDescription = null
                )
            }
        }
    }
}

@Immutable
data class QueenGameParams(
    val boardParams: BoardParams,
    val tiles: Map<BoardPosition, QueenGameTileParams>
)

@Immutable
data class QueenGameTileParams(
    val icon: Painter?,
    val backgroundColor: Color
)