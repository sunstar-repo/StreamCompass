package com.sunstar.streamcompass.presentation.core

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun statusBarProtectionHeight(): Dp {
    val density = LocalDensity.current
    return with(density) { (WindowInsets.statusBars.getTop(density) * STATUS_BAR_PROTECTION_HEIGHT_FACTOR).toDp() }
}

private const val STATUS_BAR_PROTECTION_HEIGHT_FACTOR = 1.2f
