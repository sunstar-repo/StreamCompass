package com.sunstar.streamcompass.presentation.search

import androidx.compose.runtime.Composable

// Desktop(JVM)에는 시스템 back 개념이 없어 jvm actual은 Esc 키(KeyEvent)로 처리한다.
@Composable
expect fun SearchBackHandler(enabled: Boolean, onBack: () -> Unit, content: @Composable () -> Unit)
