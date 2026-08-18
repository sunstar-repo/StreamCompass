package com.sunstar.streamcompass.domain.usecase

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetRecommendationsUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(tmdbId: Int, streamType: StreamType): Flow<PagingData<Stream>> =
        streamRepository.getRecommendationsStreamFlow(tmdbId = tmdbId, streamType = streamType)
}
