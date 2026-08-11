package com.jaxjack.queens.features.queengame.configuration

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaxjack.queens.styleguide.R
import kotlin.math.floor


@Composable
internal fun GameConfigurationScreen(
    modifier: Modifier,
    onPlayClick: () -> Unit,
) {
    val viewModel: GameConfigurationViewModel = hiltViewModel()

    val minimumBoxSize = 48.dp
    val (screenWidth, screenHeight) = LocalWindowInfo.current.containerDpSize

    LaunchedEffect(Unit) {
        val biggerValue = if (screenWidth < screenHeight) screenWidth else screenHeight
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
        onPlayClick = onPlayClick
    )
}

@Composable
private fun QueenGameContent(
    modifier: Modifier,
    params: GameConfigurationParams,
    onDecreaseBoardSizeButtonClick: () -> Unit,
    onIncreaseBoardSizeButtonClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(16.dp))
        ConfigureBoardContent(
            boardSizeText = params.boardSizeText,
            decreaseButtonContainerColor = params.decreaseButtonContainerColor,
            decreaseButtonEnabled = params.decreaseButtonEnabled,
            increaseButtonContainerColor = params.increaseButtonContainerColor,
            increaseButtonEnabled = params.increaseButtonEnabled,
            onDecreaseBoardSizeButtonClick = onDecreaseBoardSizeButtonClick,
            onIncreaseBoardSizeButtonClick = onIncreaseBoardSizeButtonClick
        )
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                disabledContentColor = MaterialTheme.colorScheme.onTertiary
            ),
            onClick = onPlayClick,
            content = {
                Text(
                    text = stringResource(R.string.game_configuration_play_button),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )
    }
}

@Composable
private fun ConfigureBoardContent(
    boardSizeText: String,
    decreaseButtonContainerColor: Color,
    decreaseButtonEnabled: Boolean,
    increaseButtonContainerColor: Color,
    increaseButtonEnabled: Boolean,
    onDecreaseBoardSizeButtonClick: () -> Unit,
    onIncreaseBoardSizeButtonClick: () -> Unit,
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.game_configuration_board_size),
        style = MaterialTheme.typography.headlineLarge.copy(
            textAlign = TextAlign.Center
        )
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoardSizeChangeButton(
            icon = painterResource(R.drawable.ic_minus),
            enabled = decreaseButtonEnabled,
            containerColor = decreaseButtonContainerColor,
            onClick = onDecreaseBoardSizeButtonClick
        )

        Spacer(modifier = Modifier.width(32.dp))

        Text(
            text = boardSizeText,
            style = MaterialTheme.typography.headlineLarge.copy(
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.width(32.dp))

        BoardSizeChangeButton(
            icon = painterResource(R.drawable.ic_add),
            containerColor = increaseButtonContainerColor,
            enabled = increaseButtonEnabled,
            onClick = onIncreaseBoardSizeButtonClick
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
    val decreaseButtonContainerColor: Color,
    val decreaseButtonEnabled: Boolean,
    val increaseButtonContainerColor: Color,
    val increaseButtonEnabled: Boolean
)
