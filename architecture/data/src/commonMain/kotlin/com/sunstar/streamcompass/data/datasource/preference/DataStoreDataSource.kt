package com.sunstar.streamcompass.data.datasource.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val THEME_MODE_KEY = intPreferencesKey("theme_mode")

internal class DataStoreDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    fun getThemeModeId(): Flow<Int?> =
        dataStore.data.map { preferences -> preferences[THEME_MODE_KEY] }

    suspend fun setThemeModeId(id: Int) {
        dataStore.edit { preferences -> preferences[THEME_MODE_KEY] = id }
    }
}
