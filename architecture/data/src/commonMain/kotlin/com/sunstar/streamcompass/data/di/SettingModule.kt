package com.sunstar.streamcompass.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sunstar.streamcompass.data.datasource.preference.DataStoreDataSource
import com.sunstar.streamcompass.data.datasource.preference.createPreferencesDataStore
import com.sunstar.streamcompass.data.repository.SettingRepositoryImpl
import com.sunstar.streamcompass.domain.repository.SettingRepository
import com.sunstar.streamcompass.domain.usecase.GetThemeModeUseCase
import com.sunstar.streamcompass.domain.usecase.SetThemeModeUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val SETTING_REPOSITORY = named("settingRepository")
private val PREFERENCES_DATA_STORE = named("preferencesDataStore")
private val DATA_STORE_DATA_SOURCE = named("dataStoreDataSource")

val settingModule =
    module {
        single<DataStore<Preferences>>(PREFERENCES_DATA_STORE) { createPreferencesDataStore() }
        single(DATA_STORE_DATA_SOURCE) {
            DataStoreDataSource(dataStore = get(qualifier = PREFERENCES_DATA_STORE))
        }
        single<SettingRepository>(SETTING_REPOSITORY) {
            SettingRepositoryImpl(dataStoreDataSource = get(qualifier = DATA_STORE_DATA_SOURCE))
        }
        single { GetThemeModeUseCase(settingRepository = get(qualifier = SETTING_REPOSITORY)) }
        single { SetThemeModeUseCase(settingRepository = get(qualifier = SETTING_REPOSITORY)) }
    }
