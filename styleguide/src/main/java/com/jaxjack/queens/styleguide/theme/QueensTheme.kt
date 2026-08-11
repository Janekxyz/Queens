package com.jaxjack.queens.styleguide.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.jaxjack.queens.styleguide.theme.base.Pink40
import com.jaxjack.queens.styleguide.theme.base.Purple40
import com.jaxjack.queens.styleguide.theme.base.PurpleGrey40
import com.jaxjack.queens.styleguide.theme.base.Typography

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun QueensTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}