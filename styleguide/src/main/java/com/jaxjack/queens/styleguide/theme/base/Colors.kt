package com.jaxjack.queens.styleguide.theme.base

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LightColorScheme = QueensColors(
    background = white,
    surface = gray,
    content = black,
    border = charcoal,
    overlay = charcoalLight,
    tileLight = whiteCreamy,
    tileDark = green,
    conflict = red,
)

internal val DarkColorScheme = QueensColors(
    background = white,
    surface = gray,
    content = black,
    border = charcoal,
    overlay = charcoalLight,
    tileLight = whiteCreamy,
    tileDark = green,
    conflict = red,
)

@Stable
class QueensColors(
    background: Color,
    surface: Color,
    content: Color,
    border: Color,
    overlay: Color,
    tileLight: Color,
    tileDark: Color,
    conflict: Color
) {

    var background by mutableStateOf(background)
        internal set

    var surface by mutableStateOf(surface)
        internal set

    var content by mutableStateOf(content)
        internal set

    var border by mutableStateOf(border)
        internal set

    var tileLight by mutableStateOf(tileLight)
        internal set

    var tileDark by mutableStateOf(tileDark)
        internal set

    var overlay by mutableStateOf(overlay)
        internal set

    var conflict by mutableStateOf(conflict)
        internal set


    fun copy(): QueensColors = QueensColors(
        background = background,
        surface = surface,
        content = content,
        border = border,
        tileLight = tileLight,
        tileDark = tileDark,
        overlay = overlay,
        conflict = conflict
    )

    fun update(other: QueensColors) {
        background = other.background
        surface = other.surface
        content = other.content
        border = other.border
        tileLight = other.tileLight
        tileDark = other.tileDark
        overlay = other.overlay
        conflict = other.conflict
    }
}

internal val LocalWorkoutsColors =
    staticCompositionLocalOf<QueensColors> { error("No QueensColors provided") }
