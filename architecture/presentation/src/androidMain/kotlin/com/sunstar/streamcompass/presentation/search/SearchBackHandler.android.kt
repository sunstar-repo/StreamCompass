package com.sunstar.streamcompass.presentation.search

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun SearchBackHandler(enabled: Boolean, onBack: () -> Unit, content: @Composable () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
    content()
}
