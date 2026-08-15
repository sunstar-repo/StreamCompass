package com.sunstar.streamcompass.domain.usecase

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetMovieRecommendationsUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(tmdbId: Int): Flow<PagingData<MovieStream>> =
        streamRepository.getMovieRecommendationsStreamFlow(tmdbId = tmdbId)
}
