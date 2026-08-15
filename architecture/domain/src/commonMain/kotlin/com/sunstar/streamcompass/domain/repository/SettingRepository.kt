package com.sunstar.streamcompass.domain.repository

import com.sunstar.streamcompass.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    fun getThemeModeFlow(): Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)
}
