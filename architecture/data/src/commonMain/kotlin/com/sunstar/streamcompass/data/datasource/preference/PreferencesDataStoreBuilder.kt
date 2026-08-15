package com.sunstar.streamcompass.data.datasource.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath

internal expect fun getPreferencesDataStorePath(): String

internal fun createPreferencesDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { getPreferencesDataStorePath().toPath() })
