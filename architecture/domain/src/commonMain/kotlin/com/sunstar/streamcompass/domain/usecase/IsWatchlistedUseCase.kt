package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.repository.StreamRepository

class IsWatchlistedUseCase(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(tmdbId: Int, streamType: StreamType): Boolean =
        streamRepository.isWatchlisted(tmdbId = tmdbId, streamType = streamType)
}
