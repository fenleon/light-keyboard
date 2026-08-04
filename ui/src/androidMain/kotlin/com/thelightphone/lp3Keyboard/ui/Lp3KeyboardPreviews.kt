    package com.thelightphone.lp3Keyboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.thelightphone.lp3Keyboard.ui.layout.ArStandard
import com.thelightphone.lp3Keyboard.ui.layout.EnQwerty
import com.thelightphone.lp3Keyboard.ui.layout.EnShared
import com.thelightphone.lp3Keyboard.ui.viewmodel.defaultEmojis

@Preview(name = "Dark", widthDp = (1080 / 3), heightDp = (1240 / 3))
@Composable
fun Lp3KeyboardDarkPreview() {
    Lp3KeyboardTheme(DarkKeyboardColors) {
        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
            val keyboardOptions = KeyboardOptions(
                defaultEmojis,
                displayReturn = true,
                displayVoice = true,
                enableKeyAnimation = true,
                swipeEnabled = true
            )
            val layoutOptions = LayoutOptions(displayCloseButton = true)
            Lp3KeyboardWrapper(
                EnShared.EmojiLayout,
                keyboardOptions,
                layoutOptions,
                previewCallback,
                null
            )
        }
    }
}

@Preview(name = "Arabic", widthDp = (1080 / 3), heightDp = (1240 / 3))
@Composable
fun Lp3KeyboardArabicPreview() {
    Lp3KeyboardTheme(DarkKeyboardColors) {
        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
            val keyboardOptions = KeyboardOptions(
                defaultEmojis,
                displayReturn = true,
                displayVoice = true,
                enableKeyAnimation = true,
                swipeEnabled = true
            )
            val layoutOptions = LayoutOptions(displayCloseButton = true)
            Lp3KeyboardWrapper(
                ArStandard.LettersLayout,
                keyboardOptions,
                layoutOptions,
                previewCallback,
                null
            )
        }
    }
}

@Preview(name = "Light", widthDp = (1080 / 3), heightDp = (1240 / 3))
@Composable
fun Lp3KeyboardLightPreview() {
    Lp3KeyboardTheme(LightKeyboardColors) {
        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
            val keyboardOptions = KeyboardOptions(
                defaultEmojis,
                displayReturn = true,
                displayVoice = true,
                enableKeyAnimation = true,
                swipeEnabled = true
            )
            val layoutOptions = LayoutOptions(displayCloseButton = true)
            Lp3KeyboardWrapper(
                ArStandard.LettersLayout,
                keyboardOptions,
                layoutOptions,
                previewCallback,
                null
            )
        }
    }
}

@Preview(name = "Wrapper", widthDp = (1080 / 3), heightDp = (1240 / 3))
@Composable
fun Lp3KeyboardWrapperPreview() {
    Lp3KeyboardTheme(DarkKeyboardColors) {
        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
            val keyboardOptions = KeyboardOptions(
                defaultEmojis,
                displayReturn = true,
                displayVoice = true,
                enableKeyAnimation = true,
                swipeEnabled = true
            )
            val layoutOptions = LayoutOptions(displayCloseButton = true)
            Lp3KeyboardWrapper(EnQwerty.LowerCaseLayout, keyboardOptions, layoutOptions, previewCallback, null)
        }
    }
}
