package com.sunstar.streamcompass.data.repository

import com.sunstar.streamcompass.data.datasource.preference.DataStoreDataSource
import com.sunstar.streamcompass.domain.model.ThemeMode
import com.sunstar.streamcompass.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SettingRepositoryImpl(
    private val dataStoreDataSource: DataStoreDataSource,
) : SettingRepository {

    override fun getThemeModeFlow(): Flow<ThemeMode> =
        dataStoreDataSource.getThemeModeId().map { id -> ThemeMode.from(id = id ?: ThemeMode.System.id) }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStoreDataSource.setThemeModeId(id = mode.id)
    }
}
