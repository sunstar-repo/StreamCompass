package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.repository.StreamRepository

class RemoveMovieHistoryUseCase(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(tmdbId: Int) = streamRepository.removeMovieHistory(tmdbId = tmdbId)
}
