package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.repository.RemoteConfigRepository

class InitializeAppUseCase(
    private val remoteConfigRepository: RemoteConfigRepository,
) {
    suspend operator fun invoke() = remoteConfigRepository.initialize()
}
