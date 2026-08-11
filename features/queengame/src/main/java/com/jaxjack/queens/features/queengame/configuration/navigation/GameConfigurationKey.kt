package com.jaxjack.queens.features.queengame.configuration.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Route key for the board setup screen. Owned by this module, resolved by :app. */
@Serializable
data object GameConfigurationKey : NavKey
