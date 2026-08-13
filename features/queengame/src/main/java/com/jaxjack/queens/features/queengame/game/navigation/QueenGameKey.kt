package com.jaxjack.queens.features.queengame.game.navigation

import androidx.navigation3.runtime.NavKey
import com.jaxjack.queens.features.queengame.QueenColor
import kotlinx.serialization.Serializable

@Serializable
data class QueenGameKey(
    val boardSize: Int,
    val queenColor: QueenColor,
) : NavKey
