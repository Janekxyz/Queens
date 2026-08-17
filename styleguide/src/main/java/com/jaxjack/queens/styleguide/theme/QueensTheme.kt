package com.jaxjack.queens.styleguide.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.jaxjack.queens.styleguide.theme.base.BaseTheme
import com.jaxjack.queens.styleguide.theme.base.DarkColorScheme
import com.jaxjack.queens.styleguide.theme.base.LightColorScheme
import com.jaxjack.queens.styleguide.theme.base.LocalWorkoutsColors
import com.jaxjack.queens.styleguide.theme.base.QueensColors

@Composable
fun QueensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    BaseTheme(
        colors = colorScheme,
        content = content
    )
}


object QueensTheme {

    val colors: QueensColors
        @Composable @ReadOnlyComposable get() = LocalWorkoutsColors.current
}
