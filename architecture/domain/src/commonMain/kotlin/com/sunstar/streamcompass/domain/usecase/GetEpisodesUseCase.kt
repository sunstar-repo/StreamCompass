package com.sunstar.streamcompass.domain.usecase

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Episode
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetEpisodesUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(tmdbId: Int, seasonNumber: Int, locale: String): Flow<PagingData<Episode>> =
        streamRepository.getEpisodesStreamFlow(tmdbId = tmdbId, seasonNumber = seasonNumber, locale = locale)
}
