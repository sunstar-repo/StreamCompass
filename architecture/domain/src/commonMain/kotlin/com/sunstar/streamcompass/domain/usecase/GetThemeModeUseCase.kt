package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.ThemeMode
import com.sunstar.streamcompass.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow

class GetThemeModeUseCase(
    private val settingRepository: SettingRepository,
) {
    operator fun invoke(): Flow<ThemeMode> = settingRepository.getThemeModeFlow()
}
