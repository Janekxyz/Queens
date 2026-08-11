package com.jaxjack.queens.features.queengame.configuration

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun GameConfigurationViewState.toParams(): GameConfigurationParams {
    return GameConfigurationParams(
        boardSizeText = boardSize.toString(),
        decreaseButtonContainerColor = if (decreaseButtonEnabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.tertiary
        },
        decreaseButtonEnabled = decreaseButtonEnabled,
        increaseButtonContainerColor = if (increaseButtonEnabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.tertiary
        },
        increaseButtonEnabled = increaseButtonEnabled
    )
}