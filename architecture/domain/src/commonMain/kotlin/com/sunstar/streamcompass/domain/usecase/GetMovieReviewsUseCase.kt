package com.sunstar.streamcompass.domain.usecase

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetMovieReviewsUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(tmdbId: Int): Flow<PagingData<Review>> =
        streamRepository.getMovieReviewsStreamFlow(tmdbId = tmdbId)
}
