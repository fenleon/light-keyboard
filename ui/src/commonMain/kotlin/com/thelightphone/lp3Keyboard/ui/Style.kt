package com.thelightphone.lp3Keyboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Resolves the platform's best-effort Akkurat font family. The .ttf/.otf files are
 * license-restricted, so we can't ship them as a common resource. Each platform resolves
 * this independently (e.g. Android scans system/consumer-bundled fonts); platforms with no
 * way to source the font fall back to [FontFamily.Default].
 */
@Composable
expect fun lightFontFamily(): FontFamily

@Immutable
data class Lp3KeyboardColors(
    val background: Color,
    val foreground: Color,
)

val DarkKeyboardColors = Lp3KeyboardColors(
    background = Color.Black,
    foreground = Color.White,
)

val LightKeyboardColors = Lp3KeyboardColors(
    background = Color.White,
    foreground = Color.Black,
)

val LocalKeyboardColors = staticCompositionLocalOf { DarkKeyboardColors }

/**
 * Provided by [Lp3Keyboard] after one runtime lookup; key composables read
 * from it instead of calling [lightFontFamily] themselves so the system-font
 * scan only happens once per keyboard, not once per key.
 */
internal val LocalAkkuratFamily = staticCompositionLocalOf<FontFamily> { FontFamily.Default }

/** Font size for a standard letter key's label. Override via [CompositionLocalProvider]. */
val LocalKeyTextSize = staticCompositionLocalOf<TextUnit> { STANDARD_KEY_TEXT_SP.sp }

@Composable
fun Lp3KeyboardTheme(
    colors: Lp3KeyboardColors = DarkKeyboardColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalKeyboardColors provides colors) {
        content()
    }
}
