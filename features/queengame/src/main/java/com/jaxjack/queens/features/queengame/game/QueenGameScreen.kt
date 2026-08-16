package com.jaxjack.queens.features.queengame.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaxjack.queens.board.BoardPosition
import com.jaxjack.queens.board.ui.BoardContent
import com.jaxjack.queens.board.ui.BoardParams
import com.jaxjack.queens.styleguide.R
import com.jaxjack.queens.styleguide.theme.QueensTheme
import com.jaxjack.queens.styleguide.theme.base.queenBlack

@Composable
internal fun QueenGameScreen(
    modifier: Modifier,
    viewModel: QueenGameViewModel,
    onBackClick: () -> Unit,
    onNextGameClick: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val elapsed = viewModel.elapsedMilliseconds.collectAsStateWithLifecycle()
    val params = state.toParams()

    QueenGameContent(
        modifier = modifier,
        params = params,
        timerParams = { elapsed.value.toTimerParams() },
        onBackClick = onBackClick,
        onNextGameClick = onNextGameClick,
        onRestartClick = { viewModel.onAction(QueenGameAction.RestartClick) },
        onTileClick = { viewModel.onAction(QueenGameAction.TileClick(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QueenGameContent(
    modifier: Modifier,
    params: QueenGameParams,
    timerParams: () -> QueenGameTimerParams,
    onBackClick: () -> Unit,
    onNextGameClick: () -> Unit,
    onRestartClick: () -> Unit,
    onTileClick: (BoardPosition) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
                )
        ) {
            TopAppBar(
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.content_description_back),
                            tint = MaterialTheme.colorScheme.onPrimaryFixed
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRestartClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_restart),
                            contentDescription = stringResource(R.string.content_description_restart),
                            tint = MaterialTheme.colorScheme.onPrimaryFixed
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(36.dp),
                            painter = painterResource(R.drawable.ic_queen),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = params.headerParams.queensLeft,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_duration),
                    contentDescription = stringResource(R.string.content_description_duration)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = timerParams().time,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
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

        params.successParams?.let { successParams ->
            QueenGameSuccessOverlay(
                params = successParams,
                onRestartClick = onRestartClick,
                onNextGameClick = onNextGameClick
            )
        }
    }
}

@Composable
private fun QueenGameSuccessOverlay(
    params: QueenGameSuccessParams,
    onRestartClick: () -> Unit,
    onNextGameClick: () -> Unit
) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val scrimAlpha by animateFloatAsState(if (shown) 0.6f else 0f, tween(220))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = scrimAlpha))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = shown,
            enter = fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.85f)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(28.dp),
                color = QueensTheme.colors.tileLight
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(color = QueensTheme.colors.tileDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(0.55f),
                            painter = painterResource(R.drawable.ic_queen),
                            contentDescription = null,
                            tint = params.queenTint
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = params.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = queenBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = params.time,
                        style = MaterialTheme.typography.displaySmall,
                        color = QueensTheme.colors.tileDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = params.message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center
                        ),
                        color = queenBlack
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = QueensTheme.colors.tileDark,
                            contentColor = QueensTheme.colors.tileLight
                        ),
                        onClick = onNextGameClick
                    ) {
                        Text(
                            text = params.nextGameButtonText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = QueensTheme.colors.tileDark),
                        onClick = onRestartClick
                    ) {
                        Text(
                            text = params.restartButtonText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
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
            .clickable(onClick = onTileClick)
            .semantics { contentDescription = params.contentDescription },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = params.attackedDotColor != null,
            enter = fadeIn(tween(100)),
            exit = fadeOut(tween(100))
        ) {
            params.attackedDotColor?.let { dotColor ->
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.25f)
                        .clip(CircleShape)
                        .background(color = dotColor)
                )
            }
        }
        AnimatedVisibility(
            visible = params.icon != null,
            enter = fadeIn(tween(100)),
            exit = fadeOut(tween(100))
        ) {
            params.icon?.let {
                Icon(
                    modifier = Modifier.fillMaxSize(0.8f),
                    painter = params.icon,
                    tint = params.iconTint,
                    contentDescription = null
                )
            }
        }
    }
}

@Immutable
data class QueenGameParams(
    val headerParams: QueenGameHeaderParams,
    val boardParams: BoardParams,
    val tiles: Map<BoardPosition, QueenGameTileParams>,
    val successParams: QueenGameSuccessParams?
)

@Immutable
data class QueenGameSuccessParams(
    val title: String,
    val message: String,
    val time: String,
    val queenTint: Color,
    val restartButtonText: String,
    val nextGameButtonText: String
)

@Immutable
data class QueenGameHeaderParams(
    val queensLeft: String
)

@Immutable
data class QueenGameTimerParams(
    val time: String
)

@Immutable
data class QueenGameTileParams(
    val contentDescription: String,
    val icon: Painter?,
    val iconTint: Color,
    val backgroundColor: Color,
    val attackedDotColor: Color?
)