package com.jaxjack.queens.features.queengame

import androidx.compose.ui.graphics.Color
import com.jaxjack.queens.styleguide.theme.base.queenBlack
import com.jaxjack.queens.styleguide.theme.base.queenWhite
import kotlinx.serialization.Serializable

@Serializable
enum class QueenColor {
    White,
    Black,
}

internal val QueenColor.tint: Color
    get() = when (this) {
        QueenColor.White -> queenWhite
        QueenColor.Black -> queenBlack
    }
