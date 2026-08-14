package com.jaxjack.queens.features.leaderboard.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jaxjack.queens.styleguide.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeaderboardScreen(
    modifier: Modifier,
    onBackClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            windowInsets = WindowInsets.statusBars,
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
            title = {
                Text(
                    text = stringResource(R.string.leaderboard_title),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.leaderboard_empty),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
