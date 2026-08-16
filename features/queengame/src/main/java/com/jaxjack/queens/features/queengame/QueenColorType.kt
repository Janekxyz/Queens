package com.jaxjack.queens.features.queengame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.jaxjack.queens.styleguide.theme.QueensTheme
import kotlinx.serialization.Serializable

@Serializable
enum class QueenColor {
    White,
    Black,
}

internal val QueenColor.tint: Color
    @Composable
    @ReadOnlyComposable
    get() = when (this) {
        QueenColor.White -> QueensTheme.colors.queenLight
        QueenColor.Black -> QueensTheme.colors.queenDark
    }
