package com.sunstar.streamcompass.presentation.setting

sealed interface SettingItem {
    data object Theme : SettingItem

    companion object {
        val entries: List<SettingItem> = listOf(Theme)
    }
}
