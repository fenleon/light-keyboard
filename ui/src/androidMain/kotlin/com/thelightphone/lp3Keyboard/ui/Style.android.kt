package com.thelightphone.lp3Keyboard.ui

import android.content.Context
import android.graphics.fonts.SystemFonts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Lookup order:
 *   1. System fonts on the host device (LP3 hardware ships with Akkurat).
 *   2. A res/font copy in the consumer's app if they have one locally
 *      (resolved via getIdentifier so a missing copy is a runtime miss,
 *      not a compile error).
 *   3. FontFamily.Default.
 */
@Composable
actual fun lightFontFamily(): FontFamily {
    val context = LocalContext.current
    return remember(context) {
        systemAkkuratFonts() ?: bundledAkkuratFonts(context) ?: FontFamily.Default
    }
}

private fun systemAkkuratFonts(): FontFamily? {
    val fonts = SystemFonts.getAvailableFonts()
        .filter { it.file?.name?.startsWith("Akkurat", ignoreCase = true) == true }
        .mapNotNull { font ->
            val file = font.file ?: return@mapNotNull null
            val weight = FontWeight(font.style.weight)
            val style = if (font.style.slant != 0) FontStyle.Italic else FontStyle.Normal
            Font(file = file, weight = weight, style = style)
        }
    return if (fonts.isNotEmpty()) FontFamily(fonts) else null
}

private fun bundledAkkuratFonts(context: Context): FontFamily? {
    val res = context.resources
    val pkg = context.packageName
    fun fontId(name: String): Int = res.getIdentifier(name, "font", pkg)

    val fonts = buildList {
        fontId("akkuratll_light").takeIf { it != 0 }
            ?.let { add(Font(it, FontWeight.Light)) }
        fontId("akkuratll_regular").takeIf { it != 0 }
            ?.let { add(Font(it, FontWeight.Normal)) }
        fontId("akkuratpro_bold").takeIf { it != 0 }
            ?.let { add(Font(it, FontWeight.Bold)) }
    }
    return if (fonts.isNotEmpty()) FontFamily(fonts) else null
}
