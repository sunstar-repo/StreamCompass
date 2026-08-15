package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.ThemeMode
import com.sunstar.streamcompass.domain.repository.SettingRepository

class SetThemeModeUseCase(
    private val settingRepository: SettingRepository,
) {
    suspend operator fun invoke(mode: ThemeMode) = settingRepository.setThemeMode(mode = mode)
}
