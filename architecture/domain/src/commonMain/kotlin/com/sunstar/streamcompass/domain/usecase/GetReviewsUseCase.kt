package com.sunstar.streamcompass.domain.usecase

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetReviewsUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(tmdbId: Int, streamType: StreamType): Flow<PagingData<Review>> =
        streamRepository.getReviewsStreamFlow(tmdbId = tmdbId, streamType = streamType)
}
