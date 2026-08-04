package com.thelightphone.lp3Keyboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font

// The wasmJs Compose runtime has no OS font fallback (unlike Android), so without bundling every
// script explicitly, non-Latin text renders as blank glyphs. Akkurat only covers Latin, so Arabic
// and emoji still need dedicated fallback fonts.
//
// Listing multiple Font()s in one FontFamily does NOT give per-glyph fallback on this target —
// Compose only ever uses the first one. Real cross-script fallback on wasmJs requires explicitly
// *preloading* fallback fonts via FontFamilyResolver.preload(), which registers them as a
// last-resort used automatically whenever the primary font is missing a glyph. See
// https://github.com/JetBrains/compose-multiplatform-core/pull/1400.
//
// Noto Naskh Arabic (not Noto Sans Arabic) matches AOSP's actual system fallback for Arabic
// script (see fonts.xml's und-Arab entry), so the web preview's Arabic rendering matches what
// the real Android build looks like. Monochrome Noto Emoji is outline-only — both color builds
// of Noto Color Emoji (CBDT bitmap and COLRv1 vector) crash Skia's wasm build.
//
// primary_latin_* is resolved by the :ui build script's resolvePrimaryLatinFont task: it's the
// real (license-restricted, gitignored) Akkurat files if a dev has dropped them in
// src/wasmJsMain/localFonts/, otherwise the bundled Noto Sans fallback — see build.gradle.kts.
@OptIn(ExperimentalTextApi::class)
@Composable
actual fun lightFontFamily(): FontFamily {
    val resolver = LocalFontFamilyResolver.current
    val arabicFallback = Font(Res.font.noto_naskh_arabic)
    val emojiFallback = Font(Res.font.noto_emoji_mono)
    LaunchedEffect(resolver, arabicFallback, emojiFallback) {
        resolver.preload(FontFamily(arabicFallback))
        resolver.preload(FontFamily(emojiFallback))
    }
    return FontFamily(
        Font(Res.font.primary_latin_light, weight = FontWeight.Light),
        Font(Res.font.primary_latin_regular, weight = FontWeight.Normal),
        Font(Res.font.primary_latin_bold, weight = FontWeight.Bold),
    )
}
