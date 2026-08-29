package com.sunstar.streamcompass.presentation.search

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

// Desktop(JVM)은 시스템 back이 없어 enabled일 때만 Esc 키(KeyEvent)를 back으로 취급한다.
// enabled가 아닐 때는 focus를 가져가지 않아 SearchTextField 등 다른 focus를 방해하지 않는다.
@Composable
actual fun SearchBackHandler(enabled: Boolean, onBack: () -> Unit, content: @Composable () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(enabled) {
        if (enabled) focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (enabled && event.type == KeyEventType.KeyUp && event.key == Key.Escape) {
                    onBack()
                    true
                } else {
                    false
                }
            },
    ) {
        content()
    }
}
