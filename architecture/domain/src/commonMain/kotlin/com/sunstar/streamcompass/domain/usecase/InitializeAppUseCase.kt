package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.repository.InitializeRepository

class InitializeAppUseCase(
    private val initializeRepository: InitializeRepository,
) {
    suspend operator fun invoke() = initializeRepository.initialize()
}
