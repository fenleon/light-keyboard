package com.thelightphone.lp3Keyboard.ui.viewmodel

import com.thelightphone.lp3Keyboard.ui.KeyboardOptions
import com.thelightphone.lp3Keyboard.ui.LayoutOptions
import com.thelightphone.lp3Keyboard.ui.Lp3KeyboardSwipeCallback
import com.thelightphone.lp3Keyboard.ui.layout.EnShared
import com.thelightphone.lp3Keyboard.ui.layout.Layout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * An abstract view model for the base, shared logic for English keyboards.
 *
 * Typically, setting initial, lower, upper, and capslock layouts is enough to define a standard
 * English keyboard. Everything else — the number/symbol/emoji layers and the long-press
 * alternates — comes from [EnShared]. See [Lp3BaseViewModel] for the shared behavior.
 */
abstract class EnBaseViewModel<SwipeResult>(
    passedCallback: Lp3RepeatableKeyboardCallback,
    swipeCallback: Lp3KeyboardSwipeCallback<SwipeResult>?,
    haptic: () -> Unit = {},
    optionsForLayout: (Layout) -> LayoutOptions = {
        LayoutOptions(
            displayCloseButton = true
        )
    },
    keyboardOptionsFlow: StateFlow<KeyboardOptions> = MutableStateFlow(
        KeyboardOptions(
            defaultEmojis,
            displayReturn = true,
            displayVoice = true,
            enableKeyAnimation = true,
            swipeEnabled = false
        )
    ),
    initialLayout: Layout,
    lowerCaseLayout: Layout,
    upperCaseLayout: Layout,
    capsLockedLayout: Layout,
) : Lp3BaseViewModel<SwipeResult>(
    passedCallback = passedCallback,
    swipeCallback = swipeCallback,
    haptic = haptic,
    optionsForLayout = optionsForLayout,
    keyboardOptionsFlow = keyboardOptionsFlow,
    initialLayout = initialLayout,
    lowerCaseLayout = lowerCaseLayout,
    upperCaseLayout = upperCaseLayout,
    capsLockedLayout = capsLockedLayout,
    numberLayout = EnShared.NumberLayout,
    symbolsLayout = EnShared.SymbolsLayout,
    emojiLayout = EnShared.EmojiLayout,
    extendedCharMapping = EnShared.extendedCharMapping,
)
