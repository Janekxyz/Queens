package com.jaxjack.queens.features.queengame.game.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class QueenGameKey(val boardSize: Int) : NavKey
