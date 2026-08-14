package com.sunstar.streamcompass.domain.usecase

import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

class GetMovieHistoryStreamUseCase(
    private val streamRepository: StreamRepository,
) {
    operator fun invoke(): Flow<List<MovieStream>> = streamRepository.getMovieHistoryStreamFlow()
}
