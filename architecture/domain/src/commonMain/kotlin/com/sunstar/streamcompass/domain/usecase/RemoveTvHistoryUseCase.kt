package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.repository.StreamRepository

class RemoveTvHistoryUseCase(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(tmdbId: Int) = streamRepository.removeTvHistory(tmdbId = tmdbId)
}
