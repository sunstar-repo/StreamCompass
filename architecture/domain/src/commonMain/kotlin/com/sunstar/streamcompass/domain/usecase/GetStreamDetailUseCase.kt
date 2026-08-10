package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.repository.StreamRepository

class GetStreamDetailUseCase(
    private val streamRepository: StreamRepository,
) {
    suspend operator fun invoke(tmdbId: Int, locale: String): MovieStreamDetail =
        streamRepository.getStreamDetail(tmdbId = tmdbId, locale = locale)
}
