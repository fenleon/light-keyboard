package com.thelightphone.lp3Keyboard.ui

fun isEmojiCodePoint(cp: Int): Boolean {
    // ZWJ and variation selector-16 are combiners, not standalone glyphs.
    if (cp == 0x200D || cp == 0xFE0F) return false
    return cp in 0x1F000..0x1FFFF ||  // Most modern emoji (supplementary plane)
            cp in 0x2300..0x23FF ||   // Misc Technical (⌚ ⌛ ⏰ …)
            cp in 0x2600..0x27BF ||   // Misc Symbols, Dingbats (☀ ✨ ❤ …)
            cp in 0x2B00..0x2BFF      // Misc Symbols & Arrows
}

fun parseEmojiString(allEmojis: String?): List<Int>? {
    if (allEmojis == null) return null
    val codePoints = mutableListOf<Int>()
    var i = 0
    while (i < allEmojis.length) {
        val cp = allEmojis.codePointAtCompat(i)
        if (isEmojiCodePoint(cp)) codePoints.add(cp)
        i += codePointCharCount(cp)
    }
    return codePoints
}

// kotlin.text's codePointAt/appendCodePoint are only public on the JVM target, so common code
// (shared with wasmJs) needs its own surrogate-pair-aware equivalents.
fun String.codePointAtCompat(index: Int): Int {
    val high = this[index]
    if (high.isHighSurrogate() && index + 1 < length) {
        val low = this[index + 1]
        if (low.isLowSurrogate()) {
            return 0x10000 + (high.code - 0xD800) * 0x400 + (low.code - 0xDC00)
        }
    }
    return high.code
}

fun codePointCharCount(codePoint: Int): Int = if (codePoint > 0xFFFF) 2 else 1

fun StringBuilder.appendCodePointCompat(codePoint: Int): StringBuilder {
    if (codePoint > 0xFFFF) {
        val offset = codePoint - 0x10000
        append(((offset / 0x400) + 0xD800).toChar())
        append(((offset % 0x400) + 0xDC00).toChar())
    } else {
        append(codePoint.toChar())
    }
    return this
}