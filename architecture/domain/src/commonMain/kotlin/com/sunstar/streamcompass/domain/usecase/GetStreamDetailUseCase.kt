package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.StreamDetail
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.repository.StreamRepository

class GetStreamDetailUseCase(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(tmdbId: Int, locale: String, streamType: StreamType): StreamDetail =
        streamRepository.getStreamDetail(tmdbId = tmdbId, locale = locale, streamType = streamType)
}
