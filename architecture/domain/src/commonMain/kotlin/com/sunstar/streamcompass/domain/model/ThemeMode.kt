package com.sunstar.streamcompass.domain.model

import com.sunstar.streamcompass.core.Log

sealed class ThemeMode(val id: Int) {
    data object System : ThemeMode(1924)

    data object Light : ThemeMode(1925)

    data object Dark : ThemeMode(1926)

    companion object {
        val entries: List<ThemeMode> by lazy { listOf(System, Light, Dark) }

        fun from(id: Int): ThemeMode {
            return when (id) {
                Light.id -> Light
                Dark.id -> Dark
                else -> System
            }
        }
    }
}
