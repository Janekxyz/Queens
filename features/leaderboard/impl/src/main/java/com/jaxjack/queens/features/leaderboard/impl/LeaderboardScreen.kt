package com.jaxjack.queens.features.leaderboard.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaxjack.queens.styleguide.R

@Composable
internal fun LeaderboardScreen(
    modifier: Modifier,
    onBackClick: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val params = state.toParams()

    LeaderboardContent(
        modifier = modifier,
        params = params,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderboardContent(
    modifier: Modifier,
    params: LeaderboardParams,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = modifier
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
            when {
                params.isLoading -> CircularProgressIndicator()
                params.isError -> LeaderboardErrorContent()
                params.isEmpty -> LeaderboardEmptyContent()
                else -> LeaderboardListContent(results = params.results)
            }
        }
    }
}

@Composable
private fun LeaderboardEmptyContent() {
    Text(
        text = stringResource(R.string.leaderboard_empty),
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun LeaderboardErrorContent() {
    Text(
        text = stringResource(R.string.leaderboard_error),
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun LeaderboardListContent(
    results: List<LeaderboardItemParams>
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
        items(items = results, key = { it.id }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.game_configuration_board_size),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = it.boardSize,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = it.duration,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Immutable
data class LeaderboardParams(
    val results: List<LeaderboardItemParams>,
    val isEmpty: Boolean,
    val isError: Boolean,
    val isLoading: Boolean
)

@Immutable
data class LeaderboardItemParams(
    val id: Long,
    val boardSize: String,
    val duration: String
)