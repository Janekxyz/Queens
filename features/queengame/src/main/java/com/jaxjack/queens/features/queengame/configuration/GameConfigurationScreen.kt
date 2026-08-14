package com.jaxjack.queens.features.queengame.configuration

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaxjack.queens.features.queengame.QueenColor
import com.jaxjack.queens.styleguide.R
import com.jaxjack.queens.styleguide.theme.base.boardTileGreen
import com.jaxjack.queens.styleguide.theme.base.boardTileWhite
import kotlin.math.floor


@Composable
internal fun GameConfigurationScreen(
    modifier: Modifier,
    onPlayClick: (Int, QueenColor) -> Unit,
    onLeaderboardClick: () -> Unit
) {
    val viewModel: GameConfigurationViewModel = hiltViewModel()

    val minimumBoxSize = 48.dp
    val (screenWidth, screenHeight) = LocalWindowInfo.current.containerDpSize

    // Calculate the maximum of the squares that would fit into the screen
    LaunchedEffect(Unit) {
        val biggerValue = min(screenWidth, screenHeight)
        val maximumSizeOfBoard = floor(biggerValue / minimumBoxSize).toInt()
        viewModel.onAction(
            GameConfigurationAction.ConfigMaximumBoardSizeForScreen(
                maximumSizeOfBoard
            )
        )
    }

    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val params = state.toParams()

    QueenGameContent(
        modifier = modifier,
        params = params,
        onDecreaseBoardSizeButtonClick = { viewModel.onAction(GameConfigurationAction.DecreaseButtonClick) },
        onIncreaseBoardSizeButtonClick = { viewModel.onAction(GameConfigurationAction.IncreaseButtonClick) },
        onQueenColorClick = { viewModel.onAction(GameConfigurationAction.QueenColorClick(it)) },
        onPlayClick = { onPlayClick(state.boardSize, state.queenColor) },
        onLeaderboardClick = { onLeaderboardClick() }
    )
}

@Composable
private fun QueenGameContent(
    modifier: Modifier,
    params: GameConfigurationParams,
    onDecreaseBoardSizeButtonClick: () -> Unit,
    onIncreaseBoardSizeButtonClick: () -> Unit,
    onQueenColorClick: (QueenColor) -> Unit,
    onPlayClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    Column(
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.union(WindowInsets.statusBars)
            )
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.game_configuration_app_name),
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                modifier = Modifier.background(Color.LightGray, CircleShape),
                onClick = onLeaderboardClick
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_leaderboard),
                    // TODO add content description
                    contentDescription = null,
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        ConfigureBoardContent(
            boardSizeText = params.boardSizeText,
            boardDimensionsText = params.boardDimensionsText,
            decreaseButtonContainerColor = params.decreaseButtonContainerColor,
            decreaseButtonEnabled = params.decreaseButtonEnabled,
            increaseButtonContainerColor = params.increaseButtonContainerColor,
            increaseButtonEnabled = params.increaseButtonEnabled,
            onDecreaseBoardSizeButtonClick = onDecreaseBoardSizeButtonClick,
            onIncreaseBoardSizeButtonClick = onIncreaseBoardSizeButtonClick
        )
        Spacer(modifier = Modifier.height(24.dp))

        QueenColorContent(
            options = params.queenColorOptions,
            onQueenColorClick = onQueenColorClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = boardTileGreen,
                contentColor = boardTileWhite
            ),
            onClick = onPlayClick,
            content = {
                Text(
                    text = stringResource(R.string.game_configuration_play_button),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ConfigureBoardContent(
    boardSizeText: String,
    boardDimensionsText: String,
    decreaseButtonContainerColor: Color,
    decreaseButtonEnabled: Boolean,
    increaseButtonContainerColor: Color,
    increaseButtonEnabled: Boolean,
    onDecreaseBoardSizeButtonClick: () -> Unit,
    onIncreaseBoardSizeButtonClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(R.string.game_configuration_board_size),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = boardDimensionsText,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .background(
                    color = Color.LightGray,
                    shape = CircleShape
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoardSizeChangeButton(
                icon = painterResource(R.drawable.ic_minus),
                enabled = decreaseButtonEnabled,
                containerColor = decreaseButtonContainerColor,
                onClick = onDecreaseBoardSizeButtonClick
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = boardSizeText,
                style = MaterialTheme.typography.headlineLarge.copy(
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            BoardSizeChangeButton(
                icon = painterResource(R.drawable.ic_add),
                containerColor = increaseButtonContainerColor,
                enabled = increaseButtonEnabled,
                onClick = onIncreaseBoardSizeButtonClick
            )
        }
    }
}

@Composable
private fun QueenColorContent(
    options: List<QueenColorOptionParams>,
    onQueenColorClick: (QueenColor) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.game_configuration_queen_color),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                QueenColorOption(
                    params = option,
                    onClick = { onQueenColorClick(option.queenColor) }
                )
            }
        }
    }
}

@Composable
private fun QueenColorOption(
    params: QueenColorOptionParams,
    onClick: () -> Unit,
) {
    val borderColor by animateColorAsState(params.borderColor)
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(shape)
            .background(color = params.backgroundColor)
            .border(width = 4.dp, color = borderColor, shape = shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(0.7f),
            painter = painterResource(R.drawable.ic_queen),
            contentDescription = params.contentDescription,
            tint = params.tint
        )
    }
}

@Composable
fun BoardSizeChangeButton(
    icon: Painter,
    containerColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(containerColor)
    IconButton(
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            disabledContainerColor = containerColor
        ),
        enabled = enabled,
        onClick = onClick
    ) {
        Icon(
            painter = icon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

data class GameConfigurationParams(
    val boardSizeText: String,
    val boardDimensionsText: String,
    val decreaseButtonContainerColor: Color,
    val decreaseButtonEnabled: Boolean,
    val increaseButtonContainerColor: Color,
    val increaseButtonEnabled: Boolean,
    val queenColorOptions: List<QueenColorOptionParams>
)

data class QueenColorOptionParams(
    val queenColor: QueenColor,
    val backgroundColor: Color,
    val tint: Color,
    val borderColor: Color,
    val contentDescription: String
)
