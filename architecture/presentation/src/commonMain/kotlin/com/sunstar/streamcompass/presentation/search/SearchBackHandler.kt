package com.sunstar.streamcompass.presentation.search

import androidx.compose.runtime.Composable

// Desktop(JVM)에는 시스템 back 개념이 없어 jvm actual은 no-op — Android에서만 실제로 가로챈다.
@Composable
expect fun SearchBackHandler(enabled: Boolean, onBack: () -> Unit)
