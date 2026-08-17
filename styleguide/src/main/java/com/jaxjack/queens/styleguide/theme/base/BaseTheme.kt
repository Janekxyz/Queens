package com.jaxjack.queens.styleguide.theme.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

@Composable
internal fun BaseTheme(
    colors: QueensColors,
    content: @Composable () -> Unit,
) {
    val rememberedColors = remember { colors.copy() }.apply { update(colors) }

    CompositionLocalProvider(
        LocalWorkoutsColors provides rememberedColors,
        content = {
            content()
        }
    )
}