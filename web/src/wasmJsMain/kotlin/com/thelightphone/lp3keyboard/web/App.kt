package com.thelightphone.lp3keyboard.web

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thelightphone.lp3Keyboard.ui.DarkKeyboardColors
import com.thelightphone.lp3Keyboard.ui.LayoutOptions
import com.thelightphone.lp3Keyboard.ui.LocalKeyTextSize
import com.thelightphone.lp3Keyboard.ui.Lp3KeyboardSwipeCallback
import com.thelightphone.lp3Keyboard.ui.Lp3KeyboardTheme
import com.thelightphone.lp3Keyboard.ui.Lp3KeyboardWrapper
import com.thelightphone.lp3Keyboard.ui.Res
import com.thelightphone.lp3Keyboard.ui.STANDARD_KEY_TEXT_SP
import com.thelightphone.lp3Keyboard.ui.SpecialKey
import com.thelightphone.lp3Keyboard.ui.appendCodePointCompat
import com.thelightphone.lp3Keyboard.ui.down_lp3
import com.thelightphone.lp3Keyboard.ui.layout.LayoutRegistryItem
import com.thelightphone.lp3Keyboard.ui.layout.buildRootViewModel
import com.thelightphone.lp3Keyboard.ui.viewmodel.Lp3KeyboardViewModel
import com.thelightphone.lp3Keyboard.ui.viewmodel.Lp3RepeatableKeyboardCallback
import org.jetbrains.compose.resources.painterResource

// A thin harness for exercising the shared keyboard layouts in a browser: a layout
// picker, a text area standing in for a real input connection, and a live keyboard
// instance wired to a callback that edits that text directly (mirrors what
// IMEService does against a real InputConnection on Android).
@Composable
fun App() {
    var selectedItem by remember { mutableStateOf(LayoutRegistryItem.EnQwerty) }
    val textState = remember { mutableStateOf("") }

    val callback = remember(selectedItem) {
        object : Lp3RepeatableKeyboardCallback {
            override fun onKeyPressed(code: Int) = Unit
            override fun onSpecialKeyPressed(key: SpecialKey) = Unit
            override fun onKeyLongPressed(code: Int) = Unit
            override fun onSpecialKeyLongPressed(key: SpecialKey) = Unit

            override fun onKeyReleased(code: Int) {
                textState.value += buildString { appendCodePointCompat(code) }
            }

            override fun onKeyRepeated(code: Int) = onKeyReleased(code)

            override fun onSpecialKeyReleased(key: SpecialKey) {
                when (key) {
                    SpecialKey.Space -> textState.value += " "
                    SpecialKey.Backspace -> textState.value = textState.value.dropLastCodePoint()
                    SpecialKey.Return -> textState.value += "\n"
                    else -> Unit
                }
            }

            override fun onSpecialKeyRepeated(specialKey: SpecialKey) =
                onSpecialKeyReleased(specialKey)

            override fun onSubmitWord(word: CharSequence) {
                textState.value += word
            }
        }
    }

    val swipeCallback = remember { object : Lp3KeyboardSwipeCallback<Unit> {} }

    val viewModel = remember(selectedItem, callback) {
        selectedItem.buildRootViewModel(callback, swipeCallback) {
            LayoutOptions(displayCloseButton = !it.isRootLayout)
        }
    }
    DisposableEffect(viewModel) {
        onDispose { viewModel.cancelHeldKeys() }
    }

    Lp3KeyboardTheme(DarkKeyboardColors) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().background(Color.Gray).padding(30.dp)
        ) {
            Text("v$APP_VERSION", color = Color.White, fontSize = 22.sp)
            Text(
                text = "The keyboard web renderer is for general layout/behavior testing only. " +
                    "It is not a pixel-perfect representation of what the keyboard will look " +
                    "like on a Light Phone.",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp).width(400.dp)
            )
            LayoutPicker(selectedItem, onSelected = { selectedItem = it })
            CompositionLocalProvider(LocalLayoutDirection provides selectedItem.layoutDirection) {
                Lp3Screen(viewModel, textState)
            }
        }
    }
}

@Composable
fun Lp3Screen(viewModel: Lp3KeyboardViewModel<Unit>, textState: MutableState<String>) {
    val text by textState
    Column(
        modifier = Modifier
            .width((1080 / 3).dp)
            .height((1260 / 3).dp)
            .border(width = 1.dp, color = Color.White)
            .background(Color.Black)
    ) {
        Box(Modifier.fillMaxWidth().weight(1f).padding(10.dp)) {
            Text(
                text = text,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier .padding(16.dp)
            )
        }
        CompositionLocalProvider(LocalKeyTextSize provides STANDARD_KEY_TEXT_SP.sp) {
            Lp3KeyboardWrapper(viewModel)
        }
    }
}

private fun String.dropLastCodePoint(): String {
    if (isEmpty()) return this
    val dropCount = if (length >= 2 && this[length - 1].isLowSurrogate() &&
        this[length - 2].isHighSurrogate()
    ) 2 else 1
    return dropLast(dropCount)
}

@Composable
private fun LayoutPicker(selected: LayoutRegistryItem, onSelected: (LayoutRegistryItem) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .border(width = 1.dp, color = Color.White)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selected.label, color = Color.White, fontSize = 18.sp)
            Icon(
                painterResource(Res.drawable.down_lp3),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(start = 8.dp).size(12.dp)
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LayoutRegistryItem.entries.forEach { item ->
                DropdownMenuItem(onClick = {
                    onSelected(item)
                    expanded = false
                }) {
                    Text(item.label)
                }
            }
        }
    }
}
